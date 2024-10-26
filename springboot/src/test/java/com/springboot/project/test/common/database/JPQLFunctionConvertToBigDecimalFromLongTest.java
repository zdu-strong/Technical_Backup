package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionConvertToBigDecimalFromLongTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(new BigDecimal("12"), JPQLFunction.convertToBigDecimal(12L));
    }

}
