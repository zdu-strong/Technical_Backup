package com.springboot.project.common.longtermtask;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.springboot.project.enumerate.LongTermTaskTempWaitDurationEnum;
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
                    .timer(LongTermTaskTempWaitDurationEnum.REFRESH_INTERVAL_SECOND, TimeUnit.SECONDS)
                    .concatMap((a) -> {
                        Thread.startVirtualThread(() -> {
                            synchronized (idOfLongTermTask) {
                                this.longTermTaskService.updateLongTermTaskToRefreshUpdateDate(idOfLongTermTask);
                            }
                        }).join();

                        return Flowable.empty();
                    }).repeat().retry().subscribe();
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
}
