package com.springboot.project.common.DistributedExecutionTask;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.springboot.project.service.DistributedExecutionService;
import io.reactivex.rxjava3.core.Flowable;

@Component
public class DistributedExecutionTaskUtil {

    @Autowired
    private DistributedExecutionService distributedExecutionService;

    /**
     * The return value of the executed method will be stored in the database as a
     * json string, and will be converted into a json object or json object array
     * and returned after success. This method will return a relative url, can call
     * a get request to get the result.
     * 
     * @param runnable
     * @return
     */
    public void run(String id, Runnable runnable) {
        var subscription = Flowable.timer(1, TimeUnit.SECONDS).concatMap((a) -> {
            Thread.startVirtualThread(() -> {
                synchronized (id) {
                    this.distributedExecutionService.updateDistributedExecutionTaskToRefreshUpdateDate(id);
                }
            }).join();
            return Flowable.empty();
        }).repeat().retry().subscribe();
        try {
            runnable.run();
            subscription.dispose();
            synchronized (id) {
                this.distributedExecutionService.updateDistributedExecutionTaskByResult(id);
            }
        } catch (Throwable e) {
            subscription.dispose();
            synchronized (id) {
                this.distributedExecutionService.updateDistributedExecutionTaskByErrorMessage(id);
            }
        }
    }
}
