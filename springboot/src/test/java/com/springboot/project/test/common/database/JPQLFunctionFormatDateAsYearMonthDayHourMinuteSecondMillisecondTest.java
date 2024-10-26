package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionFormatDateAsYearMonthDayHourMinuteSecondMillisecondTest extends BaseTest {
    private String utcOffset;

    @Test
    public void test() {
        assertEquals(23, JPQLFunction.formatDateAsYearMonthDayHourMinuteSecondMillisecond(new Date(), this.utcOffset).length());
    }

    @BeforeEach
    public void beforeEach() {
        this.utcOffset = this.timeZoneUtil.getUtcOffset("Asia/Shanghai");
    }

}
