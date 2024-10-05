package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageGetRootPathTest extends BaseTest {

    @Test
    public void test() {
        String rootPath = this.storage.getRootPath();
        assertTrue(rootPath.endsWith("target/storage"));
    }
}
