package com.springboot.project.test.service.EncryptDecryptService;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class EncryptDecryptServiceDecryptByByPublicKeyOfRSATest extends BaseTest {
	private String text = "Hello, world!";
	private byte[] bytes;

	@Test
	public void test() {
		var result = this.encryptDecryptService.decryptByByPublicKeyOfRSA(this.bytes);
		Assertions.assertEquals(text, new String(result, StandardCharsets.UTF_8));
	}

	@BeforeEach
	public void beforeEach() {
		this.bytes = this.encryptDecryptService.encryptByPrivateKeyOfRSA(this.text.getBytes(StandardCharsets.UTF_8));
	}

}
