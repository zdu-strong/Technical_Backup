package com.springboot.project.enums;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.springboot.project.service.NonceService;
import com.springboot.project.service.OrganizeRelationService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.RoleOrganizeRelationService;
import com.springboot.project.service.StorageSpaceService;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
                var totalPage = SpringUtil.getBean(StorageSpaceService.class).getStorageSpaceByPagination(1L, 1L)
                        .getTotalPages();
                return totalPage;
            },
            (pageNum) -> {
                var paginationModel = SpringUtil.getBean(StorageSpaceService.class).getStorageSpaceByPagination(pageNum,
                        1L);
                for (var storageSpaceModel : paginationModel.getItems()) {
                    SpringUtil.getBean(StorageSpaceService.class).refresh(storageSpaceModel.getFolderName());
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
                var totalPage = SpringUtil.getBean(NonceService.class).getNonceByPagination(1L, 1L).getTotalPages();
                return totalPage;
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
                var totalPage = SpringUtil.getBean(OrganizeService.class).getOrganizeByPagination(1L, 1L)
                        .getTotalPages();
                return totalPage;
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

    private Supplier<Long> callbackOfGetTotalPage;

    private Consumer<Long> callbackOfExecuteTask;

    public long getTotalPage() {
        return this.callbackOfGetTotalPage.get();
    }

    public void executeTask(long pageNum) {
        this.callbackOfExecuteTask.accept(pageNum);
    }

    public static DistributedExecutionEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(DistributedExecutionEnum::getValue, value)).get();
    }

}
