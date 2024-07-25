package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageCopyFolderTest extends BaseTest {
    private File tempFolder;

    @Test
    public void test() {
        var storageFileModel = this.storage.storageResource(new FileSystemResource(tempFolder));
        assertEquals(9287,
                FileUtils.sizeOfDirectory(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath())));
        assertEquals("default.jpg",
                new File(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()), "default.jpg")
                        .getName());
        assertEquals(9287,
                new File(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()), "default.jpg")
                        .length());
    }

    @BeforeEach
    public void beforeEach() {
        this.tempFolder = this.storage.createTempFolderByDecompressingResource(
                new ClassPathResource("zip/default.zip"));
    }

    @AfterEach
    public void afterEach() {
        this.storage.delete(tempFolder);
    }
}
