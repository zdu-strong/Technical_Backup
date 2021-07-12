package com.springboot.project.test.service.EncryptDecryptService;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class EncryptDecryptServiceDecryptByByPrivateKeyOfRSATest extends BaseTest {
	private String text = "Hello, world!";
	private byte[] bytes;

	@Test
	public void test() {
		var result = this.encryptDecryptService.decryptByByPrivateKeyOfRSA(this.bytes);
		Assertions.assertEquals(text, new String(result, StandardCharsets.UTF_8));
	}

	@BeforeEach
	public void beforeEach() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, this.encryptDecryptService.getPublicKeyOfRSA());
		this.bytes = cipher.doFinal(this.text.getBytes(StandardCharsets.UTF_8));
	}

}
