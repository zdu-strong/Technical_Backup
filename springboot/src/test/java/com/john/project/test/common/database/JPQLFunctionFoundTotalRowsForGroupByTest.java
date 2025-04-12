package com.john.project.test.common.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import com.john.project.common.database.JPQLFunction;
import com.john.project.test.common.BaseTest.BaseTest;

public class JPQLFunctionFoundTotalRowsForGroupByTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(NotImplementedException.class, () -> {
            JPQLFunction.foundTotalRowsForGroupBy();
        });
    }

}
