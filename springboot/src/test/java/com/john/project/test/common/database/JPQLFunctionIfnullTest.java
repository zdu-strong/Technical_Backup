package com.john.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.john.project.common.database.JPQLFunction;
import com.john.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionIfnullTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(new BigDecimal(1), JPQLFunction.ifnull(Long.valueOf(1), 0));
        assertEquals(new BigDecimal(1), JPQLFunction.ifnull(Integer.valueOf(1), 0));
        assertEquals(new BigDecimal(1), JPQLFunction.ifnull(new BigDecimal(1), 0));
    }

}
