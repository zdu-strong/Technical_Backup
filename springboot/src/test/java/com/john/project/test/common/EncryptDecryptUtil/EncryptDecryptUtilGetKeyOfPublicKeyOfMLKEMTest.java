package com.john.project.test.common.EncryptDecryptUtil;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptUtilGetKeyOfPublicKeyOfMLKEMTest extends BaseTest {

    @Test
    public void test() {
        assertNotNull(this.encryptDecryptUtil.getKeyOfPublicKeyOfMLKEM());
    }

}
