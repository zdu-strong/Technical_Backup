package com.john.project.test.common.EncryptDecryptUtil;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptUtilGenerateSecretKeyOfAESWithPasswordTest extends BaseTest {

    private String password;

    @Test
    public void test() {
        var secretKeyOfAES = this.encryptDecryptUtil.generateSecretKeyOfAES(password);
        assertEquals(HELLO_WORLD,
                this.encryptDecryptUtil.decryptByAES(
                        this.encryptDecryptUtil.encryptByAES(HELLO_WORLD, secretKeyOfAES),
                        secretKeyOfAES));
        assertEquals(this.encryptDecryptUtil.generateSecretKeyOfAES(password),
                this.encryptDecryptUtil.generateSecretKeyOfAES(password));
    }

    @BeforeEach
    public void beforeEach() {
        this.password = this.uuidUtil.v4();
    }

}
