package com.springboot.project.common.storage;

import java.io.File;
import org.springframework.stereotype.Component;

@Component
public class BaseStorageRefresh extends BaseStorage {

    public void refreshTempFolder() {
        if (!this.cloud.enabled()) {
            return;
        }

        for (var tempFolderName : tempFolderNameList) {
            this.storageSpaceService.refresh(tempFolderName);
            if (!new File(this.getRootPath(), tempFolderName).exists()) {
                tempFolderNameList.remove(tempFolderName);
            }
        }
    }

}
