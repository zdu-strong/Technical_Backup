package com.john.project.test.service.StorageSpaceService;

import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StorageSpaceServiceCreateTest extends BaseTest {

    private String folderName;

    @Test
    public void test() {
        var result = this.storageSpaceService.create(folderName);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(folderName, result.getFolderName());
    }

    @BeforeEach
    public void beforeEach() {
        folderName = Generators.timeBasedReorderedGenerator().generate().toString();
    }

}
