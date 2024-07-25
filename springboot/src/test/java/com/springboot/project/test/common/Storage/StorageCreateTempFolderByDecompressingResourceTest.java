package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageCreateTempFolderByDecompressingResourceTest extends BaseTest {

    private Resource resource;

    @Test
    public void test() {
        File tempFolder = this.storage.createTempFolderByDecompressingResource(resource);
        assertEquals(9287, FileUtils.sizeOfDirectory(tempFolder));
        assertEquals("default.jpg", new File(tempFolder, "default.jpg").getName());
        assertEquals(9287, new File(tempFolder, "default.jpg").length());
    }

    @BeforeEach
    public void beforeEach() {
        this.resource = new ClassPathResource("zip/default.zip");
    }
}
