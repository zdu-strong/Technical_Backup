package com.john.project.test.common.EncryptDecryptUtil;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptUtilEncryptWithFixedSaltByAESTest extends BaseTest {

    @Test
    public void test() {
        var result = this.encryptDecryptUtil.encryptWithFixedSaltByAES(HELLO_WORLD);
        assertEquals(HELLO_WORLD, this.encryptDecryptUtil.decryptByAES(result));
        assertEquals(this.encryptDecryptUtil.encryptWithFixedSaltByAES(HELLO_WORLD),
                this.encryptDecryptUtil.encryptWithFixedSaltByAES(HELLO_WORLD));
    }

}
