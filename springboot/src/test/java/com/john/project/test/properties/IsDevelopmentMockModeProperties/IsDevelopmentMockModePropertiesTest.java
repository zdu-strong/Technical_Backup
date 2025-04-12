package com.john.project.test.properties.IsDevelopmentMockModeProperties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class IsDevelopmentMockModePropertiesTest extends BaseTest {

    @Test
    public void test() {
        assertTrue(this.isDevelopmentMockModeProperties.getIsDevelopmentMockMode());
    }

}
