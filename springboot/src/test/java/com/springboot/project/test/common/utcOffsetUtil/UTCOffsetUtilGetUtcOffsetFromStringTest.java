package com.springboot.project.test.common.utcOffsetUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UTCOffsetUtilGetUtcOffsetFromStringTest extends BaseTest {

    private String timeZone = "Asia/Shanghai";

    @Test
    public void test() {
        var result = this.utcOffsetUtil.getUtcOffset(timeZone);
        assertEquals("+08:00", result);
    }

}
