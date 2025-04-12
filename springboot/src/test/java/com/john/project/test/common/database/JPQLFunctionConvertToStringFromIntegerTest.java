package com.john.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.common.database.JPQLFunction;
import com.john.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionConvertToStringFromIntegerTest extends BaseTest {

    @Test
    public void test() {
        assertEquals("15", JPQLFunction.convertToString(15));
    }

}
