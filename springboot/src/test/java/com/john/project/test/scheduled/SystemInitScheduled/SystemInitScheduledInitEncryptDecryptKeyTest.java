package com.john.project.test.scheduled.SystemInitScheduled;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class SystemInitScheduledInitEncryptDecryptKeyTest extends BaseTest {

    @Test
    public void test() {
        this.systemInitScheduled.scheduled();
        assertNotNull(this.encryptDecryptService.getKeyOfAESSecretKey());
        assertNotNull(this.encryptDecryptService.getKeyOfRSAPrivateKey());
        assertNotNull(this.encryptDecryptService.getKeyOfRSAPublicKey());
    }

}
