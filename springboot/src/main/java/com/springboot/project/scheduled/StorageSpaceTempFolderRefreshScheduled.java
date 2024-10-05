package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.storage.Storage;

@Component
public class StorageSpaceTempFolderRefreshScheduled {

    @Autowired
    private Storage storage;

    @Scheduled(initialDelay = 24 * 60 * 60 * 1000, fixedDelay = 12 * 60 * 60 * 1000)
    public void scheduled() {
        this.storage.refreshTempFolder();
    }

}
