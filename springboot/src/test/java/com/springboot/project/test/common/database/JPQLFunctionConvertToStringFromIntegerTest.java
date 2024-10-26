package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionConvertToStringFromIntegerTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("15", JPQLFunction.convertToString(15));
    }

}
