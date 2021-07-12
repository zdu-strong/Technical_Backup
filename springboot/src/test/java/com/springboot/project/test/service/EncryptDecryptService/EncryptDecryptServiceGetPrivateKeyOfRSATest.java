package com.springboot.project.test.service.EncryptDecryptService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class EncryptDecryptServiceGetPrivateKeyOfRSATest extends BaseTest {

	@Test
	public void test() {
		Assertions.assertNotNull(this.encryptDecryptService.getPrivateKeyOfRSA());
	}

}
