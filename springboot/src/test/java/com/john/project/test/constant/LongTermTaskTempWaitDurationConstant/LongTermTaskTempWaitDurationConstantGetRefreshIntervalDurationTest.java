package com.john.project.test.constant.LongTermTaskTempWaitDurationConstant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.john.project.constant.LongTermTaskTempWaitDurationConstant;
import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskTempWaitDurationConstantGetRefreshIntervalDurationTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(10 * 1000, LongTermTaskTempWaitDurationConstant.REFRESH_INTERVAL_DURATION.toMillis());
    }

}
