package com.springboot.project.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.EncryptDecryptEntity;

@Service
public class EncryptDecryptService extends BaseService {

	private RSAPublicKey keyOfRSAPublicKey;
	private RSAPrivateKey keyOfRSAPrivateKey;
	private SecretKey keyOfAESSecretKey;
	private final String keyId = "a6348051-646d-4f37-a68a-75a5a28a1d67";

	public RSAPublicKey getPublicKeyOfRSA() {
		this.generateKey();
		return this.keyOfRSAPublicKey;
	}

	public RSAPrivateKey getPrivateKeyOfRSA() {
		this.generateKey();
		return this.keyOfRSAPrivateKey;
	}

	public String encryptBySecretKeyOfAESToBase64String(String text) {
		return Base64.getEncoder().encodeToString(this.encryptBySecretKeyOfAES(text.getBytes(StandardCharsets.UTF_8)));
	}

	public String decryptByBySecretKeyOfAESByBase64String(String text) {
		return new String(this.decryptByBySecretKeyOfAES(Base64.getDecoder().decode(text)), StandardCharsets.UTF_8);
	}

	public String encryptByPrivateKeyOfRSAToBase64String(String text) {
		return Base64.getEncoder().encodeToString(this.encryptByPrivateKeyOfRSA(text.getBytes(StandardCharsets.UTF_8)));
	}

	public String decryptByByPublicKeyOfRSAByBase64String(String text) {
		return new String(this.decryptByByPublicKeyOfRSA(Base64.getDecoder().decode(text)), StandardCharsets.UTF_8);
	}

	public String decryptByByPrivateKeyOfRSAByBase64String(String text) {
		return new String(this.decryptByByPrivateKeyOfRSA(Base64.getDecoder().decode(text)), StandardCharsets.UTF_8);
	}

	public SecretKey getSecretKeyOfAES() {
		this.generateKey();
		return this.keyOfAESSecretKey;
	}

	public byte[] encryptBySecretKeyOfAES(byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, this.getSecretKeyOfAES());
			return cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public byte[] encryptByPrivateKeyOfRSA(byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, this.getPrivateKeyOfRSA());
			return cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public byte[] decryptByByPrivateKeyOfRSA(byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, this.getPrivateKeyOfRSA());
			return cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public byte[] decryptByByPublicKeyOfRSA(byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, this.getPublicKeyOfRSA());
			return cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public byte[] decryptByBySecretKeyOfAES(byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, this.getSecretKeyOfAES());
			return cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void generateKey() {
		try {
			if (this.keyOfAESSecretKey == null) {
				synchronized (this.getClass()) {
					if (this.keyOfAESSecretKey == null) {
						String id = this.keyId;
						if (this.EncryptDecryptEntity().where(s -> s.getId().equals(id)).findFirst().isEmpty()) {

							EncryptDecryptEntity encryptDecryptEntity = new EncryptDecryptEntity();
							encryptDecryptEntity.setCreateDate(new Date());

							/**
							 * aes for common uses
							 */
							encryptDecryptEntity.setId(this.keyId);
							var keyGenerator = KeyGenerator.getInstance("AES");
							keyGenerator.init(256);
							encryptDecryptEntity.setSecretKeyOfAES(keyGenerator.generateKey().getEncoded());

							/**
							 * rsa for common uses
							 */
							var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
							keyPairGenerator.initialize(2048);
							var keyPair = keyPairGenerator.generateKeyPair();
							encryptDecryptEntity.setPublicKeyOfRSA(keyPair.getPublic().getEncoded());
							encryptDecryptEntity.setPrivateKeyOfRSA(keyPair.getPrivate().getEncoded());

							this.entityManager.persist(encryptDecryptEntity);
						}
						EncryptDecryptEntity encryptDecryptEntity = this.EncryptDecryptEntity()
								.where(s -> s.getId().equals(id)).getOnlyValue();
						this.keyOfRSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
								.generatePublic(new X509EncodedKeySpec(encryptDecryptEntity.getPublicKeyOfRSA()));
						this.keyOfRSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
								.generatePrivate(new PKCS8EncodedKeySpec(encryptDecryptEntity.getPrivateKeyOfRSA()));
						this.keyOfAESSecretKey = new SecretKeySpec(encryptDecryptEntity.getSecretKeyOfAES(), "AES");
					}
				}
			}
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
