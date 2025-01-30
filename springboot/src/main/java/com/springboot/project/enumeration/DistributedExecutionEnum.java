package com.springboot.project.enumeration;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.springboot.project.properties.IsDevelopmentMockModeProperties;
import com.springboot.project.service.NonceService;
import com.springboot.project.service.OrganizeRelationService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.StorageSpaceService;
import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DistributedExecutionEnum {

    /**
     * Storage space cleans up the stored data in the database
     */
    STORAGE_SPACE_CLEAN_DATABASE_STORAGE(
            Duration.ofHours(12),
            1,
            () -> {
                var totalRecord = SpringUtil.getBean(StorageSpaceService.class).getStorageSpaceByPagination(1L, 1L)
                        .getTotalRecord();
                return totalRecord;
            },
            (pageNum) -> {
                var paginationModel = SpringUtil.getBean(StorageSpaceService.class).getStorageSpaceByPagination(pageNum,
                        1L);
                for (var storageSpaceModel : paginationModel.getList()) {
                    SpringUtil.getBean(StorageSpaceService.class).refresh(storageSpaceModel.getFolderName());
                }
            }),

    /**
     * Clean outdated nonce data in the database
     */
    NONCE_CLEAN(
            Duration.ofHours(12),
            1,
            () -> {
                var totalRecord = SpringUtil.getBean(NonceService.class).getNonceByPagination(1L, 1L).getTotalRecord();
                return totalRecord;
            },
            (pageNum) -> {
                var paginationModel = SpringUtil.getBean(NonceService.class).getNonceByPagination(pageNum,
                        1L);
                for (var nonceModel : paginationModel.getList()) {
                    SpringUtil.getBean(NonceService.class).delete(nonceModel.getId());
                }
            }),

    /**
     * The OrganizeEntity refreshes the data of the OrganizeRelationEntity.
     */
    ORGANIZE_REFRESH_ORGANIZE_CLOSURE_ENTITY(
            Duration.ofMinutes(1),
            1,
            () -> {
                var totalRecord = SpringUtil.getBean(OrganizeService.class).getOrganizeByPagination(1L, 1L)
                        .getTotalRecord();
                return totalRecord;
            },
            (pageNum) -> {
                var paginationModel = SpringUtil.getBean(OrganizeService.class).getOrganizeByPagination(pageNum,
                        1L);
                for (var organizeModel : paginationModel.getList()) {
                    while (true) {
                        var hasNext = SpringUtil.getBean(OrganizeRelationService.class).refresh(organizeModel.getId());
                        if (!hasNext) {
                            break;
                        }
                    }
                }
            });

    private Duration theIntervalBetweenTwoExecutions;

    @Getter
    private Integer maxNumberOfParallel;

    private Supplier<Long> callbackOfGetTotalRecord;

    private Consumer<Long> callbackOfExecuteTask;

    public long getTotalRecord() {
        return this.callbackOfGetTotalRecord.get();
    }

    public void executeTask(long pageNum) {
        this.callbackOfExecuteTask.accept(pageNum);
    }

    public Duration getTheIntervalBetweenTwoExecutions() {
        if (SpringUtil.getBean(IsDevelopmentMockModeProperties.class).getIsDevelopmentMockMode()) {
            if (Duration.ofMinutes(1).compareTo(this.theIntervalBetweenTwoExecutions) < 0) {
                return Duration.ofMinutes(1);
            }
        }
        return this.theIntervalBetweenTwoExecutions;
    }

}
