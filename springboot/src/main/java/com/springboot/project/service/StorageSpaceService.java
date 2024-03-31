package com.springboot.project.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.enumerate.StorageSpaceEnum;

@Service
public class StorageSpaceService extends BaseService {

    public void refresh(String folderName) {
        this.checkIsValidFolderName(folderName);
        this.refreshStorageSpaceEntity(folderName);

        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(0 - StorageSpaceEnum.TEMP_FILE_SURVIVAL_DURATION.toMillis()).intValue());
        var isUsed = !this.StorageSpaceEntity()
                .where(s -> s.getFolderName().equals(folderName))
                .where(s -> s.getUpdateDate().before(expireDate))
                .exists();

        if (isUsed) {
            return;
        }
        this.storage.delete(folderName);
        if (new File(this.storage.getRootPath(), folderName).exists()) {
            throw new RuntimeException("Folder deletion failed. FolderName:" + folderName);
        }
        for (var storageSpaceEntity : this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName))
                .toList()) {
            this.remove(storageSpaceEntity);
        }
    }

    private void refreshStorageSpaceEntity(String folderName) {
        if (this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName)).exists()) {
            if (this.isUsedByProgramData(folderName)) {
                var storageSpaceEntity = this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName))
                        .getOnlyValue();
                storageSpaceEntity.setUpdateDate(new Date());
                this.merge(storageSpaceEntity);
            }
            return;
        }

        var storageSpaceEntity = new StorageSpaceEntity();
        storageSpaceEntity.setId(newId());
        storageSpaceEntity.setFolderName(folderName);
        storageSpaceEntity.setCreateDate(new Date());
        storageSpaceEntity.setUpdateDate(new Date());
        this.persist(storageSpaceEntity);
    }

    private boolean isUsedByProgramData(String folderName) {
        if (this.UserMessageEntity().where(s -> s.getFolderName().equals(folderName)).exists()) {
            return true;
        }
        return false;
    }

    private void checkIsValidFolderName(String folderName) {
        if (StringUtils.isBlank(folderName)) {
            throw new RuntimeException("Folder name cannot be empty");
        }
        if (folderName.contains("/") || folderName.contains("\\")) {
            throw new RuntimeException("Folder name is invalid");
        }
        if (Paths.get(folderName).isAbsolute()) {
            throw new RuntimeException("Folder name is invalid");
        }
    }

}
