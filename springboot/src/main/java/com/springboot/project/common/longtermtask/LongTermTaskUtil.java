package com.springboot.project.common.longtermtask;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.constant.LongTermTaskTempWaitDurationConstant;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.LongTermTaskService;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LongTermTaskUtil {

    @Autowired
    private LongTermTaskService longTermTaskService;

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    /**
     * The return value of the executed method will be stored in the database as a
     * json string, and will be converted into a json object or json object array
     * and returned after success. This method will return a relative url, can call
     * a get request to get the result.
     * 
     * @param runnable
     * @return
     */
    public ResponseEntity<String> run(Supplier<ResponseEntity<?>> supplier) {
        String idOfLongTermTask = this.longTermTaskService.createLongTermTask();
        Thread.startVirtualThread(() -> {
            var subscription = Flowable
                    .timer(LongTermTaskTempWaitDurationConstant.REFRESH_INTERVAL_DURATION.toMillis(),
                            TimeUnit.MILLISECONDS)
                    .doOnNext((a) -> {
                        Thread.startVirtualThread(() -> {
                            synchronized (idOfLongTermTask) {
                                this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(idOfLongTermTask);
                            }
                        }).join();
                    })
                    .repeat()
                    .retry()
                    .subscribe();
            try {
                var result = supplier.get();
                subscription.dispose();
                synchronized (idOfLongTermTask) {
                    this.longTermTaskService.updateLongTermTaskByResult(idOfLongTermTask, result);
                }
            } catch (Throwable e) {
                subscription.dispose();
                synchronized (idOfLongTermTask) {
                    this.longTermTaskService.updateLongTermTaskByErrorMessage(idOfLongTermTask, e);
                }
                log.error(e.getMessage(), e);
            }
        });
        return ResponseEntity.ok(this.encryptDecryptService.encryptByAES(idOfLongTermTask));
    }

    public void runSkipWhenExists(Runnable runnable, LongTermTaskUniqueKeyModel... uniqueKey) {
        this.run(runnable, false, null, uniqueKey);
    }

    public void run(
            Runnable runnable,
            ResponseStatusException expectException,
            LongTermTaskUniqueKeyModel... uniqueKey) {
        this.run(runnable, false, expectException, uniqueKey);
    }

    /**
     * The return value of the executed method will be stored in the database as a
     * json string, and will be converted into a json object or json object array
     * and returned after success. This method will return a relative url, can call
     * a get request to get the result.
     * 
     * @param runnable
     * @return
     */
    public void run(
            Runnable runnable,
            boolean isRetry,
            ResponseStatusException expectException,
            LongTermTaskUniqueKeyModel... uniqueKey) {
        var deadline = DateUtils.addSeconds(new Date(), 5);
        List<String> idListOfLongTermTask = List.of();
        while (true) {
            try {
                idListOfLongTermTask = this.longTermTaskService.createLongTermTask(uniqueKey);
                break;
            } catch (DataIntegrityViolationException | JpaSystemException e1) {
                if (isRetry) {
                    if (new Date().before(deadline)) {
                        ThreadUtils.sleepQuietly(Duration.ofMillis(1));
                        continue;
                    }
                }
                if (expectException != null) {
                    throw expectException;
                } else {
                    return;
                }
            }
        }
        var idList = idListOfLongTermTask;
        var syncKey = Generators.timeBasedReorderedGenerator().generate().toString();
        var subscription = Flowable
                .timer(LongTermTaskTempWaitDurationConstant.REFRESH_INTERVAL_DURATION.toMillis(), TimeUnit.MILLISECONDS)
                .doOnNext((a) -> {
                    Thread.startVirtualThread(() -> {
                        synchronized (syncKey) {
                            for (var id : idList) {
                                this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(id);
                            }
                        }
                    }).join();
                })
                .repeat()
                .retry()
                .subscribe();
        try {
            runnable.run();
        } finally {
            subscription.dispose();
            if (!CollectionUtils.isEmpty(idListOfLongTermTask)) {
                synchronized (syncKey) {
                    for (var idOfLongTermTask : idListOfLongTermTask) {
                        this.longTermTaskService.delete(idOfLongTermTask);
                    }
                }
            }
        }
    }
}
