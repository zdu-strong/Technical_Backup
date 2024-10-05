package com.springboot.diff.test;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import com.springboot.diff.SpringbootProjectApplication;
import com.springboot.diff.test.common.BaseTest.BaseTest;

public class SpringbootProjectApplicationTest extends BaseTest {

    @Test
    public void test() throws Throwable {
        SpringbootProjectApplication.main(ArrayUtils.EMPTY_STRING_ARRAY);
    }

}
