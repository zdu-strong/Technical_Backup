package com.john.diff.test;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import com.john.diff.SpringbootProjectApplication;
import com.john.diff.test.common.BaseTest.BaseTest;

public class SpringbootProjectApplicationTest extends BaseTest {

    @Test
    public void test() {
        SpringbootProjectApplication.main(ArrayUtils.EMPTY_STRING_ARRAY);
    }

}
