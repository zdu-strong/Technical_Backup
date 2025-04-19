package com.john.project.test.service.StorageSpaceService;

import com.john.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageSpaceServiceGetStorageSpaceByPaginationTest extends BaseTest {

    private String folderName;

    @Test
    public void test() {
        var paginationModel = this.storageSpaceService.getStorageSpaceByPagination(1L, 1L);
        assertEquals(1, paginationModel.getPageNum());
        assertEquals(1, paginationModel.getPageSize());
        assertEquals(1, paginationModel.getTotalPages());
        assertEquals(1, paginationModel.getTotalRecords());
        assertTrue(paginationModel.getItems().stream().map(s -> s.getFolderName()).toList().contains(folderName));
    }

    @BeforeEach
    public void beforeEach() {
        folderName = this.storage.createTempFolder().getName();
        Flowable.range(1, 1000)
                .filter(s -> !this.storageSpaceService.getStorageSpaceByPagination(1L, 1L).getItems().isEmpty())
                .take(1)
                .blockingSubscribe();
    }

}
