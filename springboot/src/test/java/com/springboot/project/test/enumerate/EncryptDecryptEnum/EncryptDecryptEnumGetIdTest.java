package com.springboot.project.test.enumerate.EncryptDecryptEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.enumerate.EncryptDecryptEnum;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptEnumGetIdTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(36, EncryptDecryptEnum.getId().length());
    }

}
