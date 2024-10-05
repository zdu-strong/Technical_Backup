package com.springboot.project.test.properties.DateFormatProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class DateFormatPropertiesGetYearMonthDayHourMinuteSecondTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("yyyy-MM-dd HH:mm:ss",
                this.dateFormatProperties.getYearMonthDayHourMinuteSecond());
    }

}
