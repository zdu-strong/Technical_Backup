package com.john.project.test.common.EncryptDecryptUtil;

import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptDecryptUtilDecryptByByPrivateKeyOfMLKEMTest extends BaseTest {

    private String textOfEncryptByPublicKeyOfMLKEM;

    @Test
    public void test() {
        assertEquals(HELLO_WORLD,
                this.encryptDecryptUtil.decryptByByPrivateKeyOfMLKEM(this.textOfEncryptByPublicKeyOfMLKEM));
    }

    @BeforeEach
    public void beforeEach() {
        this.textOfEncryptByPublicKeyOfMLKEM = this.encryptDecryptUtil.encryptByPublicKeyOfMLKEM(HELLO_WORLD);
    }

}
