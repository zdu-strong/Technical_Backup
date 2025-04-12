package com.john.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.john.project.common.database.JPQLFunction;
import com.john.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionConvertToStringFromBigDecimalTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("12.59",  JPQLFunction.convertToString(new BigDecimal("12.59")));
    }

}
