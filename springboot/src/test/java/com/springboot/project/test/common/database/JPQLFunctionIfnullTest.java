package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionIfnullTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.ifnull(Long.valueOf(1), 0);
        });
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.ifnull(Integer.valueOf(1), 0);
        });
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.ifnull(new BigDecimal(1), 0);
        });
    }

}
