package com.john.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceGenerateSecretKeyOfAESTest extends BaseTest {

    private String text = "Hello, World!";

    @Test
    public void test() {
        var secretKeyOfAES = this.encryptDecryptService.generateSecretKeyOfAES();
        assertEquals(text,
                this.encryptDecryptService.decryptByAES(
                        this.encryptDecryptService.encryptByAES(text, secretKeyOfAES),
                        secretKeyOfAES));
    }

}
