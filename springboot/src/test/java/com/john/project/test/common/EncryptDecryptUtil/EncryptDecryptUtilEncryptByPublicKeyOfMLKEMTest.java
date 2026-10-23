package com.john.project.test.common.EncryptDecryptUtil;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptUtilEncryptByPublicKeyOfMLKEMTest extends BaseTest {

    @Test
    public void test() {
        var result = this.encryptDecryptUtil.encryptByPublicKeyOfMLKEM(HELLO_WORLD);
        assertEquals(HELLO_WORLD,
                this.encryptDecryptUtil.decryptByByPrivateKeyOfMLKEM(result));
    }

}
