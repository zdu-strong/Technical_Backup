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
import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.enums.LongTermTaskTypeEnum;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;
import com.springboot.project.service.DistributedExecutionMainService;
import com.springboot.project.service.LongTermTaskService;
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

    @Autowired
    private LongTermTaskService longTermTaskService;

    @SneakyThrows
    public void refreshData(DistributedExecutionEnum distributedExecutionEnum) {
        var longTermTaskUniqueKeyModel = this.findOne(distributedExecutionEnum);
        if (longTermTaskUniqueKeyModel == null) {
            return;
        }
        this.longTermTaskUtil.runSkipWhenExists(() -> {
            this.refreshDataByDistributedExecutionEnum(distributedExecutionEnum);
        }, longTermTaskUniqueKeyModel);
    }

    private void refreshDataByDistributedExecutionEnum(DistributedExecutionEnum distributedExecutionEnum) {
        var now = new Date();
        while (true) {
            var distributedExecutionMainModel = this.distributedExecutionMainService
                    .getLastDoneDistributedExecution(distributedExecutionEnum);
            if (distributedExecutionMainModel != null && !distributedExecutionMainModel.getCreateDate().before(now)) {
                return;
            } else if (distributedExecutionMainModel != null && !DateUtils
                    .addMilliseconds(distributedExecutionMainModel.getUpdateDate(),
                            (int) distributedExecutionEnum.getTheIntervalBetweenTwoExecutions().toMillis())
                    .after(now)) {
                return;
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

    private LongTermTaskUniqueKeyModel findOne(DistributedExecutionEnum distributedExecutionEnum) {
        var longTermTaskUniqueKeyModelList = Flowable.range(1, distributedExecutionEnum.getMaxNumberOfParallel())
                .map(s -> new LongTermTaskUniqueKeyModel()
                        .setType(LongTermTaskTypeEnum.DISTRIBUTED_EXECUTION.getValue())
                        .setUniqueKey(
                                this.objectMapper.writeValueAsString(new Pair<>(distributedExecutionEnum.getValue(), s))))
                .toList()
                .blockingGet();
        return this.longTermTaskService.findOneNotRunning(longTermTaskUniqueKeyModelList);
    }

}
