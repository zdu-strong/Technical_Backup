package com.springboot.project.test.common.properties.IsTestOrDevModeProperties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class IsTestOrDevModePropertiesIsTestOrDevModeTest extends BaseTest {

    @Test
    public void test() {
        assertTrue(this.isTestOrDevModeProperties.getIsTestOrDevMode());
    }

}
