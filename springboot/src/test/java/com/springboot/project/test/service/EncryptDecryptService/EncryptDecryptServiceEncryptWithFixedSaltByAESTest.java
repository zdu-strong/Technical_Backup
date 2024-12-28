package com.springboot.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceEncryptWithFixedSaltByAESTest extends BaseTest {

    private String text = "Hello, World!";

    @Test
    public void test() {
        var result = this.encryptDecryptService.encryptWithFixedSaltByAES(this.text);
        assertEquals(text, this.encryptDecryptService.decryptByAES(result));
        assertEquals(this.encryptDecryptService.encryptWithFixedSaltByAES(this.text),
                this.encryptDecryptService.encryptWithFixedSaltByAES(this.text));
    }

}
