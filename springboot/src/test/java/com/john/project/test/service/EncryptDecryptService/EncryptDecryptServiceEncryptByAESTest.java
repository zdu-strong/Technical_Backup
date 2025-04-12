package com.john.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceEncryptByAESTest extends BaseTest {
    private String text = "Hello, World!";

    @Test
    public void test() {
        var result = this.encryptDecryptService.encryptByAES(this.text);
        assertEquals(text, this.encryptDecryptService.decryptByAES(result));
    }

}
