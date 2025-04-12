package com.john.project.test.properties.AliyunCloudStorageProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class AliyunCloudStoragePropertiesGetEnabledTest extends BaseTest {

    @Test
    public void test() {
        assertFalse(this.aliyunCloudStorageProperties.getEnabled());
    }

}
