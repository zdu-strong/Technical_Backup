package com.springboot.project.test.common.properties.DateFormatProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class DateFormatPropertiesGetYearTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("yyyy",
                this.dateFormatProperties.getYear());
    }

}
