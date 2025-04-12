package com.john.project.test.service.EncryptDecryptService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class EncryptDecryptServiceGetKeyOfRSAPrivateKeyTest extends BaseTest {

    @Test
    public void test() {
        assertNotNull(this.encryptDecryptService.getKeyOfRSAPrivateKey());
    }

}
