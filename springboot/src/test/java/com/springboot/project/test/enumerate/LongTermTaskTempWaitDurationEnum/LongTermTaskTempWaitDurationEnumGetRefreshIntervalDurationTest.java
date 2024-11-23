package com.springboot.project.test.enumerate.LongTermTaskTempWaitDurationEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.springboot.project.constant.LongTermTaskTempWaitDurationConstant;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskTempWaitDurationEnumGetRefreshIntervalDurationTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(10 * 1000, LongTermTaskTempWaitDurationConstant.REFRESH_INTERVAL_DURATION.toMillis());
    }

}
