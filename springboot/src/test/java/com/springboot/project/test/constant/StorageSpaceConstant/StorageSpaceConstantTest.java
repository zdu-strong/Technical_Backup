package com.springboot.project.test.constant.StorageSpaceConstant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.springboot.project.constant.StorageSpaceConstant;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageSpaceConstantTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(86400000, StorageSpaceConstant.TEMP_FILE_SURVIVAL_DURATION.toMillis());
    }

}
