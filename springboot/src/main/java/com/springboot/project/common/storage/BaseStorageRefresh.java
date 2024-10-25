package com.springboot.project.common.storage;

import org.springframework.stereotype.Component;

@Component
public class BaseStorageRefresh extends BaseStorage {

    public void refreshTempFolder() {
        if (!this.cloud.enabled()) {
            return;
        }

        for (var tempFolderName : tempFolderNameList) {
            this.storageSpaceService.refresh(tempFolderName);
            tempFolderNameList.remove(tempFolderName);
        }
    }

}
