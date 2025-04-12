package com.john.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceGenerateKeyPairOfRSATest extends BaseTest {

    private String text = "Hello, World!";

    @Test
    public void test() {
        var keyPair = this.encryptDecryptService.generateKeyPairOfRSA();
        assertEquals(text,
                this.encryptDecryptService.decryptByByPrivateKeyOfRSA(
                        this.encryptDecryptService.encryptByPublicKeyOfRSA(text, keyPair.getPublicKeyOfRSA()),
                        keyPair.getPrivateKeyOfRSA()));
    }

}
