package com.john.project.test.common.EncryptDecryptUtil;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptUtilGenerateSecretKeyOfAESTest extends BaseTest {

    @Test
    public void test() {
        var secretKeyOfAES = this.encryptDecryptUtil.generateSecretKeyOfAES();
        assertEquals(HELLO_WORLD,
                this.encryptDecryptUtil.decryptByAES(
                        this.encryptDecryptUtil.encryptByAES(HELLO_WORLD, secretKeyOfAES),
                        secretKeyOfAES));
    }

}
