package com.john.project.common.LongTermTaskUtil;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import cn.hutool.core.util.ObjectUtil;
import com.john.project.common.EncryptDecryptUtil.EncryptDecryptUtil;
import com.john.project.common.uuid.UUIDUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.service.LongTermTaskService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import static com.john.project.constant.LongTermTaskTempWaitDurationConstant.REFRESH_INTERVAL_DURATION;

@Component
@Slf4j
public class LongTermTaskUtil {

    @Autowired
    private LongTermTaskService longTermTaskService;

    @Autowired
    private EncryptDecryptUtil encryptDecryptUtil;

    @Autowired
    private Executor applicationTaskExecutor;

    @Autowired
    private UUIDUtil uuidUtil;

    /**
     * The return value of the executed method will be stored in the database as a
     * json string, and will be converted into a json object or json object array
     * and returned after success. This method will return a relative url, can call
     * a get request to get the result.
     */
    public ResponseEntity<String> run(Supplier<ResponseEntity<?>> supplier) {
        String idOfLongTermTask = this.longTermTaskService.createLongTermTask();
        Thread.startVirtualThread(() -> {
            var subscription = Flowable.timer(REFRESH_INTERVAL_DURATION.toMillis(), TimeUnit.MILLISECONDS)
                    .observeOn(Schedulers.from(applicationTaskExecutor))
                    .doOnNext((a) -> {
                        synchronized (idOfLongTermTask) {
                            this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(idOfLongTermTask);
                        }
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
        return ResponseEntity.ok(this.encryptDecryptUtil.encryptByAES(idOfLongTermTask));
    }

    public void runSkipWhenExists(Runnable runnable, LongTermTaskUniqueKeyModel... uniqueKey) {
        this.run(runnable, false, null, uniqueKey);
    }

    public void runRetryWhenExists(Runnable runnable,
                                   ResponseStatusException expectException,
                                   LongTermTaskUniqueKeyModel... uniqueKey) {
        this.run(runnable, true, expectException, uniqueKey);
    }

    public void runSkipAfterRetryWhenExists(Runnable runnable, LongTermTaskUniqueKeyModel... uniqueKey) {
        this.run(runnable, true, null, uniqueKey);
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
    @SneakyThrows
    private void run(
            Runnable runnable,
            boolean isRetry,
            ResponseStatusException expectException,
            LongTermTaskUniqueKeyModel... uniqueKey) {
        var deadline = DateUtils.addSeconds(new Date(), 5);
        List<String> idListOfLongTermTask;
        while (true) {
            if (ObjectUtil.isNull(this.longTermTaskService.findOneNotRunning(List.of(uniqueKey)))) {
                if (isRetry) {
                    if (new Date().before(deadline)) {
                        ThreadUtils.sleepQuietly(Duration.ofMillis(100));
                        continue;
                    }
                }
                if (expectException != null) {
                    throw expectException;
                } else {
                    return;
                }
            }

            try {
                this.longTermTaskService.deleteLongTermTaskOfExpired(uniqueKey);
                idListOfLongTermTask = this.longTermTaskService.createLongTermTask(uniqueKey);
                break;
            } catch (DataIntegrityViolationException | JpaSystemException e1) {
                if (isRetry) {
                    if (new Date().before(deadline)) {
                        ThreadUtils.sleepQuietly(Duration.ofMillis(100));
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
        var syncKey = this.uuidUtil.v4();
        var subscription = Flowable.timer(REFRESH_INTERVAL_DURATION.toMillis(), TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.from(applicationTaskExecutor))
                .doOnNext((a) -> {
                    synchronized (syncKey) {
                        for (var id : idList) {
                            this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(id);
                        }
                    }
                })
                .repeat()
                .retry()
                .subscribe();
        try {
            runnable.run();
        } finally {
            subscription.dispose();
            synchronized (syncKey) {
                for (var id : idList) {
                    this.longTermTaskService.delete(id);
                }
            }
        }
    }

}
