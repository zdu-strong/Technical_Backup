package com.john.project.test.properties.SchedulingPoolSizeProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class SchedulingPoolSizePropertiesGetSchedulingPoolSizeTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(50,
                this.schedulingPoolSizeProperties.getSchedulingPoolSize());
    }

}
