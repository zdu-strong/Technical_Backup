package com.springboot.project.common.DistributedExecutionUtil;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.enumerate.LongTermTaskTypeEnum;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;
import com.springboot.project.service.DistributedExecutionMainService;
import cn.hutool.core.thread.ThreadUtil;
import com.springboot.project.service.DistributedExecutionDetailService;
import io.reactivex.rxjava3.core.Flowable;
import lombok.SneakyThrows;

@Component
public class DistributedExecutionUtil {

    @Autowired
    private DistributedExecutionMainService distributedExecutionMainService;

    @Autowired
    private DistributedExecutionDetailService distributedExecutionTaskService;

    @Autowired
    private LongTermTaskUtil longTermTaskUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    public void refreshData(DistributedExecutionEnum distributedExecutionEnum) {
        var longTermTaskUniqueKeyModel = new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.DISTRIBUTED_EXECUTION.name())
                .setUniqueKey(this.objectMapper.writeValueAsString(new Pair<>(distributedExecutionEnum.name(), 1)));
        this.longTermTaskUtil.run(() -> {
            this.refreshDataByDistributedExecutionEnum(
                    DistributedExecutionEnum.ORGANIZE_REFRESH_ORGANIZE_CLOSURE_ENTITY);
        }, null, longTermTaskUniqueKeyModel);
    }

    private void refreshDataByDistributedExecutionEnum(DistributedExecutionEnum distributedExecutionEnum) {
        var now = new Date();
        while (true) {
            // The execution time interval has not expired, take a break
            var distributedExecutionMainModel = this.distributedExecutionMainService
                    .getLastSuccessDistributedExecution(distributedExecutionEnum);
            if (distributedExecutionMainModel != null) {
                if (!distributedExecutionMainModel.getCreateDate().before(now)) {
                    break;
                }
                if (DateUtils
                        .addMilliseconds(distributedExecutionMainModel.getCreateDate(),
                                (int) distributedExecutionEnum.getTheIntervalBetweenTwoExecutions().toMillis())
                        .after(now)) {
                    ThreadUtil.sleep(DateUtils
                            .addMilliseconds(distributedExecutionMainModel.getCreateDate(),
                                    (int) distributedExecutionEnum.getTheIntervalBetweenTwoExecutions().toMillis())
                            .getTime() - now.getTime());
                    continue;
                }
            }
            var distributedExecutionMainId = getId(distributedExecutionEnum);
            this.refreshSingleData(distributedExecutionMainId, distributedExecutionEnum);
        }
    }

    private String getId(DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionMainModel = this.distributedExecutionMainService
                .getDistributedExecutionWithInprogress(distributedExecutionEnum);
        if (distributedExecutionMainModel != null) {
            return distributedExecutionMainModel.getId();
        }
        var id = this.distributedExecutionMainService
                .create(distributedExecutionEnum, distributedExecutionEnum.getTotalRecord()).getId();
        return id;
    }

    /**
     * 
     * @param distributedExecutionEnum
     * @return isDone boolean
     */
    private void refreshSingleData(String distributedExecutionMainId,
            DistributedExecutionEnum distributedExecutionEnum) {
        var pageNum = this.distributedExecutionTaskService
                .getPageNumForExecution(distributedExecutionMainId);
        if (pageNum == null) {
            ThreadUtils.sleepQuietly(Duration.ofMillis(1000));
            return;
        }

        String id;
        try {
            id = this.distributedExecutionTaskService.create(distributedExecutionMainId, pageNum).getId();
        } catch (DataIntegrityViolationException | JpaSystemException e) {
            ThreadUtils.sleepQuietly(Duration.ofMillis(1000));
            return;
        }

        var subscription = Flowable.timer(5, TimeUnit.SECONDS)
                .doOnNext((a) -> {
                    Thread.startVirtualThread(() -> {
                        synchronized (id) {
                            this.distributedExecutionTaskService.refreshUpdateDate(id);
                        }
                    }).join();
                })
                .repeat()
                .retry()
                .subscribe();
        try {
            distributedExecutionEnum.executeTask(pageNum);
            subscription.dispose();
            synchronized (id) {
                this.distributedExecutionTaskService.updateByResult(id);
            }
        } catch (Throwable e) {
            subscription.dispose();
            synchronized (id) {
                this.distributedExecutionTaskService.updateByErrorMessage(id);
            }
        }
    }

}
