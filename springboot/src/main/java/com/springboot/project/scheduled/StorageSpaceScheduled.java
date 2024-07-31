package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.springboot.project.enumerate.DistributedExecutionEnum;

@Component
public class StorageSpaceScheduled {

    @Autowired
    private DistributedExecutionUtil distributedExecutionUtil;

    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 60 * 60 * 1000)
    public void scheduled() {
        this.cleanDatabaseStorage();
    }

    private void cleanDatabaseStorage() {
        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE);
    }
}
