package com.john.project.test.service.EncryptDecryptService;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceGenerateKeyPairOfMLKEMTest extends BaseTest {

    @Test
    public void test() {
        var keyPair = this.encryptDecryptService.generateKeyPairOfMLKEM();
        assertEquals(HELLO_WORLD,
                this.encryptDecryptService.decryptByByPrivateKeyOfMLKEM(
                        this.encryptDecryptService.encryptByPublicKeyOfMLKEM(HELLO_WORLD, keyPair.getPublicKeyOfMLKEM()),
                        keyPair.getPrivateKeyOfMLKEM()));
    }

}
