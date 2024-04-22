package com.springboot.project.scheduled;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.project.common.DistributedExecutionTask.DistributedExecutionTaskUtil;
import com.springboot.project.service.DistributedExecutionService;
import com.springboot.project.service.StorageSpaceService;

@Component
public class StorageSpaceScheduled {

    @Autowired
    private StorageSpaceService storageSpaceService;

    @Autowired
    private DistributedExecutionService distributedExecutionService;

    @Autowired
    private DistributedExecutionTaskUtil distributedExecutionTaskUtil;

    @Scheduled(initialDelay = 60 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    public void scheduled() throws InterruptedException, ExecutionException {
        this.cleanDatabaseStorage();
    }

    public void cleanDatabaseStorage() {
        while (true) {
            var distributedExecutionModel = this.distributedExecutionService.getDistributedExecutionOfStorageSpace();
            if (distributedExecutionModel == null) {
                return;
            }
            this.distributedExecutionTaskUtil.run(distributedExecutionModel.getId(), () -> {
                for (var storageSpaceModel : distributedExecutionModel.getPagination().getList()) {
                    this.storageSpaceService.refresh(storageSpaceModel.getFolderName());
                }
            });
        }
    }
}
