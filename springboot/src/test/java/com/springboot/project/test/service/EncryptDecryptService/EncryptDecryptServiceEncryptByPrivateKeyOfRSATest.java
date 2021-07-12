package com.springboot.project.test.service.EncryptDecryptService;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class EncryptDecryptServiceEncryptByPrivateKeyOfRSATest extends BaseTest {
	private String text = "Hello, world!";

	@Test
	public void test() {
		var result = this.encryptDecryptService.encryptByPrivateKeyOfRSA(this.text.getBytes(StandardCharsets.UTF_8));
		Assertions.assertEquals(text,
				new String(this.encryptDecryptService.decryptByByPublicKeyOfRSA(result), StandardCharsets.UTF_8));
	}

}
