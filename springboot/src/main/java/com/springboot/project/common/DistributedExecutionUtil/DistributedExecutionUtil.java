package com.springboot.project.common.DistributedExecutionUtil;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.service.DistributedExecutionService;
import com.springboot.project.service.DistributedExecutionTaskService;
import io.reactivex.rxjava3.core.Flowable;

@Component
public class DistributedExecutionUtil {

    @Autowired
    private DistributedExecutionService distributedExecutionService;

    @Autowired
    private DistributedExecutionTaskService distributedExecutionTaskService;

    public void refreshData(DistributedExecutionEnum distributedExecutionEnum) {
        var now = new Date();
        while (true) {
            var isDone = this.refreshSingleData(distributedExecutionEnum, now);
            if (isDone) {
                return;
            }
        }
    }

    /**
     * 
     * @param distributedExecutionEnum
     * @return isDone boolean
     */
    private boolean refreshSingleData(DistributedExecutionEnum distributedExecutionEnum, Date now) {
        var lastDistributedExecutionModel = distributedExecutionService
                .getLastDistributedExecution(distributedExecutionEnum);
        if (lastDistributedExecutionModel != null && lastDistributedExecutionModel.getIsDone()) {
            var lastExecutionDate = DateUtils.addMilliseconds(lastDistributedExecutionModel.getUpdateDate(),
                    (int) distributedExecutionEnum.getTheIntervalBetweenTwoExecutions().toMillis());
            if (new Date().before(lastExecutionDate)) {
                if (!now.after(lastDistributedExecutionModel.getCreateDate())) {
                    return true;
                } else {
                    ThreadUtils.sleepQuietly(
                            Duration.ofMillis(Math.max(lastExecutionDate.getTime() - new Date().getTime(), 0)));
                    return false;
                }
            }
        }
        if (lastDistributedExecutionModel == null || lastDistributedExecutionModel.getIsDone()) {
            lastDistributedExecutionModel = this.distributedExecutionService.create(distributedExecutionEnum,
                    distributedExecutionEnum.getTotalRecord());
        }

        if (lastDistributedExecutionModel.getIsDone()) {
            return false;
        }

        var pageNum = this.distributedExecutionTaskService
                .getPageNumForExecution(lastDistributedExecutionModel.getId());
        if (pageNum == null) {
            ThreadUtils.sleepQuietly(Duration.ofMillis(1000));
            return false;
        }

        String id;
        try {
            id = this.distributedExecutionTaskService.create(lastDistributedExecutionModel.getId(), pageNum).getId();
        } catch (DataIntegrityViolationException | JpaSystemException e) {
            ThreadUtils.sleepQuietly(Duration.ofMillis(1000));
            return false;
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
        return false;
    }

}
