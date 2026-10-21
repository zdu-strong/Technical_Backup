package com.john.project.test.service.EncryptDecryptService;

import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptDecryptServiceDecryptByByPrivateKeyOfMLKEMTest extends BaseTest {

    private String textOfEncryptByPublicKeyOfMLKEM;

    @Test
    public void test() {
        assertEquals(HELLO_WORLD,
                this.encryptDecryptService.decryptByByPrivateKeyOfMLKEM(this.textOfEncryptByPublicKeyOfMLKEM));
    }

    @BeforeEach
    public void beforeEach() {
        this.textOfEncryptByPublicKeyOfMLKEM = this.encryptDecryptService.encryptByPublicKeyOfMLKEM(HELLO_WORLD);
    }

}
