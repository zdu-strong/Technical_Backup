package com.john.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.common.database.JPQLFunction;
import com.john.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionFormatDateAsYearMonthDayHourMinuteTest extends BaseTest {
    private String utcOffset;

    @Test
    public void test() {
        assertEquals(16, JPQLFunction.formatDateAsYearMonthDayHourMinute(new Date(), this.utcOffset).length());
    }

    @BeforeEach
    public void beforeEach() {
        this.utcOffset = this.timeZoneUtil.getUtcOffset("Asia/Shanghai");
    }

}
