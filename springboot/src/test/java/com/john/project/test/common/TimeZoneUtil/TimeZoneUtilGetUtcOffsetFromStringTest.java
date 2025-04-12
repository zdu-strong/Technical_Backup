package com.john.project.test.common.TimeZoneUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilGetUtcOffsetFromStringTest extends BaseTest {

    private String timeZone = "Asia/Shanghai";

    @Test
    public void test() {
        var result = this.timeZoneUtil.getUtcOffset(timeZone);
        assertEquals("+08:00", result);
    }

}
