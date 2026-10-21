package com.john.project.test.service.EncryptDecryptService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceEncryptByPublicKeyOfMLKEMTest extends BaseTest {

    @Test
    public void test() {
        var result = this.encryptDecryptService.encryptByPublicKeyOfMLKEM(HELLO_WORLD);
        assertEquals(HELLO_WORLD,
                this.encryptDecryptService.decryptByByPrivateKeyOfMLKEM(result));
    }

}
