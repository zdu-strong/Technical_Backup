package com.springboot.project.test.constant.EncryptDecryptConstant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.springboot.project.constant.EncryptDecryptConstant;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptConstantGetIdTest extends BaseTest {

    @Test
    public void test() {
        assertEquals(36, EncryptDecryptConstant.getId().length());
    }

}
