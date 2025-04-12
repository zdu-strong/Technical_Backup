package com.john.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.john.project.test.common.BaseTest.BaseTest;

public class StorageCopyResourceTest extends BaseTest {
    private Resource resource;

    @Test
    public void test() {
        var storageFileModel = this.storage.storageResource(this.resource);
        assertEquals(9287, this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).length());
        assertEquals("default.jpg",
                this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).getName());
    }

    @BeforeEach
    public void beforeEach() {
        this.resource = new ClassPathResource("image/default.jpg");
    }
}
