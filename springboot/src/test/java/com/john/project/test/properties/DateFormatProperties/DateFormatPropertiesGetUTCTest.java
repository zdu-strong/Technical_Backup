package com.john.project.test.properties.DateFormatProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class DateFormatPropertiesGetUTCTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                this.dateFormatProperties.getUTC());
    }

}
