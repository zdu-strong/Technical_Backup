package com.john.project.test.service.StorageSpaceService;

import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StorageSpaceServiceRefreshTest extends BaseTest {

    private String folderName;

    @Test
    public void test() {
        this.storageSpaceService.refresh(folderName);
    }

    @BeforeEach
    public void beforeEach() {
        folderName = Generators.timeBasedReorderedGenerator().generate().toString();
    }

}
