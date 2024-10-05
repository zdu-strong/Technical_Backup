package com.springboot.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceDecryptByByPublicKeyOfRSATest extends BaseTest {
    private String text = "Hello, world!";
    private String textOfEncryptByPrivateKeyOfRSA;

    @Test
    public void test() {
        assertEquals(text,
                this.encryptDecryptService.decryptByByPublicKeyOfRSA(this.textOfEncryptByPrivateKeyOfRSA));
    }

    @BeforeEach
    public void beforeEach() {
        this.textOfEncryptByPrivateKeyOfRSA = this.encryptDecryptService.encryptByPrivateKeyOfRSA(text);
    }

}
