package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageDeleteTest extends BaseTest {
    private File tempFolder;

    @Test
    public void test() {
        this.storage.delete(this.tempFolder);
        assertFalse(this.tempFolder.isDirectory());
    }

    @BeforeEach
    public void beforeEach() {
        this.tempFolder = this.storage.createTempFolder();
    }
}
