package com.john.project.test.common.TimeZoneUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilGetUtcOffsetFromTimeZoneTest extends BaseTest {

    private TimeZone timeZone;

    @Test
    public void test() {
        var result = this.timeZoneUtil.getUtcOffset(this.timeZone);
        assertEquals("+08:00", result);
    }

    @BeforeEach
    public void beforeEach() {
        this.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
    }

}
