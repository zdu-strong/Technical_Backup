package com.john.project.service;

import java.util.Date;

import com.john.project.entity.StorageSpaceEntity;
import com.john.project.entity.UserMessageEntity;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.john.project.common.baseService.BaseService;
import com.john.project.constant.StorageSpaceConstant;
import com.john.project.model.PaginationModel;
import com.john.project.model.StorageSpaceModel;

@Service
public class StorageSpaceService extends BaseService {

    @SneakyThrows
    @Transactional(readOnly = true)
    public boolean isUsedByProgramData(String folderName) {
        if (isUsedByUserMessageEntity(folderName)) {
            return true;
        }
        return false;
    }

    public StorageSpaceModel create(String folderName) {
        var storageSpaceEntity = new StorageSpaceEntity();
        storageSpaceEntity.setId(newId());
        storageSpaceEntity.setFolderName(folderName);
        storageSpaceEntity.setCreateDate(new Date());
        storageSpaceEntity.setUpdateDate(new Date());
        this.persist(storageSpaceEntity);
        return this.storageSpaceFormatter.format(storageSpaceEntity);
    }

    public void update(String folderName) {
        var storageSpaceOptional = this.streamAll(StorageSpaceEntity.class)
                .where(s -> s.getFolderName().equals(folderName))
                .findOne();
        if (storageSpaceOptional.isEmpty()) {
            return;
        }
        var storageSpaceEntity = storageSpaceOptional.get();
        storageSpaceEntity.setUpdateDate(new Date());
        this.merge(storageSpaceEntity);
    }

    public void delete(String folderName) {
        for (var storageSpaceEntity : this.streamAll(StorageSpaceEntity.class)
                .where(s -> s.getFolderName().equals(folderName))
                .toList()) {
            this.remove(storageSpaceEntity);
        }
    }

    @Transactional(readOnly = true)
    public PaginationModel<StorageSpaceModel> getStorageSpaceByPagination(Long pageNum, Long pageSize) {
        var stream = this.streamAll(StorageSpaceEntity.class)
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.storageSpaceFormatter.format(s));
    }

    @Transactional(readOnly = true)
    public boolean isUsed(String folderName) {
        return isUsedByProgramData(folderName) || isUsedByTempFile(folderName);
    }

    @Transactional(readOnly = true)
    public boolean isUsedByTempFile(String folderName) {
        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(0 - StorageSpaceConstant.TEMP_FILE_SURVIVAL_DURATION.toMillis()).intValue());
        var isUsed = !this.streamAll(StorageSpaceEntity.class)
                .where(s -> s.getFolderName().equals(folderName))
                .where(s -> s.getUpdateDate().before(expireDate))
                .exists();
        return isUsed;
    }

    private boolean isUsedByUserMessageEntity(String folderName) {
        var isUsed = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getFolderName().equals(folderName))
                .exists();
        return isUsed;
    }

}
