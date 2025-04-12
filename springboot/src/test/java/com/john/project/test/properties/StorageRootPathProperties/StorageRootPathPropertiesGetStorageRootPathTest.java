package com.john.project.test.properties.StorageRootPathProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class StorageRootPathPropertiesGetStorageRootPathTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a",
                this.storageRootPathProperties.getStorageRootPath());
    }

}
