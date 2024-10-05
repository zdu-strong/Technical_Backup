package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageCreateTempFolderTest extends BaseTest {
    @Test
    public void test() {
        File tempFolder = this.storage.createTempFolder();
        assertTrue(tempFolder.isDirectory());
        assertTrue(tempFolder.getAbsolutePath().replaceAll(Pattern.quote("\\"), "/")
                .startsWith(this.storage.getRootPath()));
        assertEquals(36, tempFolder.getName().length());
    }
}
