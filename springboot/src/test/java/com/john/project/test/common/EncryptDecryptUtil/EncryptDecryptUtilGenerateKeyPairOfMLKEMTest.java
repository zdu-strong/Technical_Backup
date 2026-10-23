package com.john.project.test.common.EncryptDecryptUtil;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptUtilGenerateKeyPairOfMLKEMTest extends BaseTest {

    @Test
    public void test() {
        var keyPair = this.encryptDecryptUtil.generateKeyPairOfMLKEM();
        assertEquals(HELLO_WORLD,
                this.encryptDecryptUtil.decryptByByPrivateKeyOfMLKEM(
                        this.encryptDecryptUtil.encryptByPublicKeyOfMLKEM(HELLO_WORLD, keyPair.getPublicKeyOfMLKEM()),
                        keyPair.getPrivateKeyOfMLKEM()));
    }

}
