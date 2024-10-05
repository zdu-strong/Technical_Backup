package com.springboot.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionFoundTotalRowsForGroupByTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(RuntimeException.class, () -> {
            JPQLFunction.foundTotalRowsForGroupBy();
        });
    }

}
