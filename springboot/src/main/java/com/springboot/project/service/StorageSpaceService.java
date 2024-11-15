package com.springboot.project.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.enumerate.StorageSpaceEnum;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.StorageSpaceModel;
import cn.hutool.core.text.StrFormatter;

@Service
public class StorageSpaceService extends BaseService {

    public void refresh(String folderName) {
        this.checkHasValidOfFolderName(folderName);
        this.refreshStorageSpaceEntity(folderName);

        if (isUsed(folderName)) {
            return;
        }

        this.delete(folderName);
    }

    @Transactional(readOnly = true)
    public PaginationModel<StorageSpaceModel> getStorageSpaceByPagination(Long pageNum, Long pageSize) {
        var stream = this.StorageSpaceEntity()
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.storageSpaceFormatter.format(s));
    }

    private boolean isUsedByProgramData(String folderName) {
        if (isUsedByUserMessageEntity(folderName)) {
            return true;
        }
        return false;
    }

    private boolean isUsedByUserMessageEntity(String folderName) {
        var isUsed = this.UserMessageEntity().where(s -> s.getFolderName().equals(folderName)).exists();
        return isUsed;
    }

    private void delete(String folderName) {
        var request = new MockHttpServletRequest();
        request.setRequestURI(this.storage.getResoureUrlFromResourcePath(folderName));
        this.storage.delete(request);
        if (new File(this.storage.getRootPath(), folderName).exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    StrFormatter.format("Folder deletion failed. FolderName:{}", folderName));
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

    private boolean isUsed(String folderName) {
        return isUsedByProgramData(folderName) || isUsedByTempFile(folderName);
    }

    private boolean isUsedByTempFile(String folderName) {
        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(0 - StorageSpaceEnum.TEMP_FILE_SURVIVAL_DURATION.toMillis()).intValue());
        var isUsed = !this.StorageSpaceEntity()
                .where(s -> s.getFolderName().equals(folderName))
                .where(s -> s.getUpdateDate().before(expireDate))
                .exists();
        return isUsed;
    }

    private void checkHasValidOfFolderName(String folderName) {
        if (StringUtils.isBlank(folderName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder name cannot be empty");
        }
        if (folderName.contains("/") || folderName.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder name is invalid");
        }
        if (Paths.get(folderName).isAbsolute()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder name is invalid");
        }
    }

}
