package com.john.project.enums;

import java.io.File;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import cn.hutool.core.text.StrFormatter;
import com.john.project.common.longtermtask.LongTermTaskUtil;
import com.john.project.common.storage.Storage;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.model.PaginationModel;
import com.john.project.service.NonceService;
import com.john.project.service.OrganizeRelationService;
import com.john.project.service.OrganizeService;
import com.john.project.service.RoleOrganizeRelationService;
import com.john.project.service.StorageSpaceService;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
public enum DistributedExecutionEnum {

    /**
     * Storage space cleans up the stored data in the database
     */
    STORAGE_SPACE_CLEAN(
            "STORAGE_SPACE_CLEAN",
            Duration.ofHours(12),
            1L,
            () -> {
                return SpringUtil.getBean(StorageSpaceService.class).getStorageSpaceByPagination(1L, 1L);
            },
            (pageNum) -> {
                var storageSpaceService = SpringUtil.getBean(StorageSpaceService.class);
                var storage = SpringUtil.getBean(Storage.class);
                var longTermTaskUtil = SpringUtil.getBean(LongTermTaskUtil.class);
                var paginationModel = storageSpaceService.getStorageSpaceByPagination(pageNum,
                        1L);
                for (var storageSpaceModel : paginationModel.getItems()) {
                    var folderName = storageSpaceModel.getFolderName();
                    var longTermTaskUniqueKeyModel = new LongTermTaskUniqueKeyModel()
                            .setType(LongTermTaskTypeEnum.REFRESH_STORAGE_SPACE.getValue())
                            .setUniqueKey(folderName);
                    if (storageSpaceService.isUsedByProgramData(folderName)) {
                        storageSpaceService.update(folderName);
                    } else {
                        longTermTaskUtil.runSkipWhenExists(() -> {
                            if (!storageSpaceService.isUsed(folderName)) {
                                var request = new MockHttpServletRequest();
                                request.setRequestURI(storage.getResoureUrlFromResourcePath(folderName));
                                storage.delete(request);
                                if (new File(storage.getRootPath(), folderName).exists()) {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                            StrFormatter.format("Folder deletion failed. FolderName:{}", folderName));
                                }
                                storageSpaceService.delete(folderName);
                            }
                        }, longTermTaskUniqueKeyModel);
                    }
                }
            }),

    /**
     * Clean outdated nonce data in the database
     */
    NONCE_CLEAN(
            "NONCE_CLEAN",
            Duration.ofHours(12),
            1L,
            () -> {
                return SpringUtil.getBean(NonceService.class).getNonceByPagination(1L, 1L);
            },
            (pageNum) -> {
                var paginationModel = SpringUtil.getBean(NonceService.class).getNonceByPagination(pageNum,
                        1L);
                for (var nonceModel : paginationModel.getItems()) {
                    SpringUtil.getBean(NonceService.class).delete(nonceModel.getId());
                }
            }),

    /**
     * The OrganizeEntity refreshes the data of the OrganizeRelationEntity.
     */
    ORGANIZE_CLOSURE_REFRESH(
            "ORGANIZE_CLOSURE_REFRESH",
            Duration.ofMinutes(1),
            1L,
            () -> {
                return SpringUtil.getBean(OrganizeService.class).getOrganizeByPagination(1L, 1L);
            },
            (pageNum) -> {
                var paginationModel = SpringUtil.getBean(OrganizeService.class).getOrganizeByPagination(pageNum,
                        1L);
                for (var organizeModel : paginationModel.getItems()) {
                    while (true) {
                        var hasNext = SpringUtil.getBean(RoleOrganizeRelationService.class)
                                .refresh(organizeModel.getId());
                        if (!hasNext) {
                            break;
                        }
                    }
                    while (true) {
                        var hasNext = SpringUtil.getBean(OrganizeRelationService.class).refresh(organizeModel.getId());
                        if (!hasNext) {
                            break;
                        }
                    }
                }
            });

    @Getter
    private String value;

    @Getter
    private Duration theIntervalBetweenTwoExecutions;

    @Getter
    private long maxNumberOfParallel;

    private Supplier<PaginationModel<?>> callbackOfGetPagination;

    private Consumer<Long> callbackOfExecuteTask;

    public PaginationModel<?> getPagination() {
        return this.callbackOfGetPagination.get();
    }

    public void executeTask(long pageNum) {
        this.callbackOfExecuteTask.accept(pageNum);
    }

    public static DistributedExecutionEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(DistributedExecutionEnum::getValue, value)).get();
    }

}
