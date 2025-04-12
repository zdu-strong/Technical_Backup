package com.john.project.test.common.TimeZoneUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class TimeZoneUtilCheckTimeZoneTest extends BaseTest {

    private String timeZone;

    @Test
    public void test() {
        this.timeZoneUtil.checkTimeZone(this.timeZone);
    }

    @BeforeEach
    public void beforeEach() {
        this.timeZone = "Asia/Shanghai";
    }

}
