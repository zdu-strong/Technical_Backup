package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.enumerate.LongTermTaskTypeEnum;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;

@Component
public class StorageSpaceScheduled {

    @Autowired
    private DistributedExecutionUtil distributedExecutionUtil;

    @Autowired
    private LongTermTaskUtil longTermTaskUtil;

    @Autowired
    private GitProperties gitProperties;

    @Scheduled(initialDelay = 60 * 60 * 1000, fixedDelay = 12 * 60 * 60 * 1000)
    public void scheduled() {
        var longTermTaskUniqueKeyModel = new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE.getType())
                .setUniqueKey(gitProperties.getCommitId());
        this.longTermTaskUtil.run(() -> {
            this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE);
        }, null, longTermTaskUniqueKeyModel);
    }

}
