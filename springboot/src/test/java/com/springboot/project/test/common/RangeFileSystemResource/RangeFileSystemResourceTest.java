package com.springboot.project.test.common.RangeFileSystemResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.springboot.project.common.StorageResource.RangeFileSystemResource;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class RangeFileSystemResourceTest extends BaseTest {
    private File tempFile;

    @Test
    public void test() {
        var rangeFileSystemResource = new RangeFileSystemResource(tempFile, 800, 5);
        assertEquals(5, rangeFileSystemResource.contentLength());
        assertEquals("default.jpg", this.storage.getFileNameFromResource(rangeFileSystemResource));
    }

    @BeforeEach
    public void beforeEach() {
        this.tempFile = this.storage
                .createTempFileOrFolder(new ClassPathResource("image/default.jpg"));
    }
}
