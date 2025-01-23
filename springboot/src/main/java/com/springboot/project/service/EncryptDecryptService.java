package com.springboot.project.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.constant.EncryptDecryptConstant;
import com.springboot.project.entity.EncryptDecryptEntity;
import com.springboot.project.model.EncryptDecryptModel;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import io.reactivex.rxjava3.core.Flowable;
import lombok.SneakyThrows;

@Service
public class EncryptDecryptService extends BaseService {

    private RSAPublicKey keyOfRSAPublicKey;
    private RSAPrivateKey keyOfRSAPrivateKey;
    private SecretKey keyOfAESSecretKey;
    private Boolean ready = false;

    @Transactional(readOnly = true)
    public String encryptByAES(String text) {
        var secretKeyOfAES = Base64.getEncoder().encodeToString(this.getKeyOfAESSecretKey().getEncoded());
        return this.encryptByAES(text, secretKeyOfAES);
    }

    @Transactional(readOnly = true)
    public String decryptByAES(String text) {
        var secretKeyOfAES = Base64.getEncoder().encodeToString(this.getKeyOfAESSecretKey().getEncoded());
        return this.decryptByAES(text, secretKeyOfAES);
    }

    @Transactional(readOnly = true)
    public String encryptByPrivateKeyOfRSA(String text) {
        var rsa = new RSA(this.getKeyOfRSAPrivateKey(), this.getKeyOfRSAPublicKey());
        return rsa.encryptBase64(text, KeyType.PrivateKey);
    }

    @Transactional(readOnly = true)
    public String encryptByPublicKeyOfRSA(String text) {
        var rsa = new RSA(this.getKeyOfRSAPrivateKey(), this.getKeyOfRSAPublicKey());
        return rsa.encryptBase64(text, KeyType.PublicKey);
    }

    @Transactional(readOnly = true)
    public String decryptByByPublicKeyOfRSA(String text) {
        var rsa = new RSA(this.getKeyOfRSAPrivateKey(), this.getKeyOfRSAPublicKey());
        return rsa.decryptStr(text, KeyType.PublicKey);
    }

    @Transactional(readOnly = true)
    public String decryptByByPrivateKeyOfRSA(String text) {
        var rsa = new RSA(this.getKeyOfRSAPrivateKey(), this.getKeyOfRSAPublicKey());
        return rsa.decryptStr(text, KeyType.PrivateKey);
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public String encryptByAES(String text, String secretKeyOfAES) {
        var salt = DigestUtils.md5(Generators.timeBasedReorderedGenerator().generate().toString());
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(
                Base64.getDecoder().decode(secretKeyOfAES), "AES"), new GCMParameterSpec(16 * 8, salt));
        return Base64.getEncoder()
                .encodeToString(ArrayUtils.addAll(salt, cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public String decryptByAES(String text, String secretKeyOfAES) {
        var textByteList = Base64.getDecoder().decode(text);
        var salt = ArrayUtils.subarray(textByteList, 0, 16);
        var encryptedTextByteList = ArrayUtils.subarray(textByteList, 16, textByteList.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(
                Base64.getDecoder().decode(secretKeyOfAES), "AES"), new GCMParameterSpec(16 * 8, salt));
        return new String(cipher.doFinal(encryptedTextByteList), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public String encryptByPrivateKeyOfRSA(String text, String privateKeyOfRSA) {
        var keyOfRSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(
                        Base64.getDecoder().decode(privateKeyOfRSA)));
        var rsa = new RSA(keyOfRSAPrivateKey, null);
        return rsa.encryptBase64(text, KeyType.PrivateKey);
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public String encryptByPublicKeyOfRSA(String text, String publicKeyOfRSA) {
        var keyOfRSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(
                        Base64.getDecoder().decode(publicKeyOfRSA)));
        var rsa = new RSA(null, keyOfRSAPublicKey);
        return rsa.encryptBase64(text, KeyType.PublicKey);
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public String decryptByByPublicKeyOfRSA(String text, String publicKeyOfRSA) {
        var keyOfRSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(
                        Base64.getDecoder().decode(publicKeyOfRSA)));
        var rsa = new RSA(null, keyOfRSAPublicKey);
        return rsa.decryptStr(text, KeyType.PublicKey);
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public String decryptByByPrivateKeyOfRSA(String text, String privateKeyOfRSA) {
        var keyOfRSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(
                        Base64.getDecoder().decode(privateKeyOfRSA)));
        var rsa = new RSA(keyOfRSAPrivateKey, null);
        return rsa.decryptStr(text, KeyType.PrivateKey);
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public String generateSecretKeyOfAES(String password) {
        var salt = DigestUtils.md5(password);
        var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        var spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        var secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return Base64.getEncoder().encodeToString(secret.getEncoded());
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public String generateSecretKeyOfAES() {
        var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public EncryptDecryptModel generateKeyPairOfRSA() {
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        var keyPair = keyPairGenerator.generateKeyPair();
        return new EncryptDecryptModel()
                .setPublicKeyOfRSA(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()))
                .setPrivateKeyOfRSA(
                        Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public String encryptWithFixedSaltByAES(String text) {
        var salt = DigestUtils.md5(text);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, this.getKeyOfAESSecretKey(), new GCMParameterSpec(16 * 8, salt));
        return Base64.getEncoder()
                .encodeToString(ArrayUtils.addAll(salt, cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
    }

    @Transactional(readOnly = true)
    public RSAPrivateKey getKeyOfRSAPrivateKey() {
        this.initKey();
        return this.keyOfRSAPrivateKey;
    }

    @Transactional(readOnly = true)
    public RSAPublicKey getKeyOfRSAPublicKey() {
        this.initKey();
        return this.keyOfRSAPublicKey;
    }

    @Transactional(readOnly = true)
    public SecretKey getKeyOfAESSecretKey() {
        this.initKey();
        return this.keyOfAESSecretKey;
    }

    @SneakyThrows
    private void initKey() {
        if (this.ready) {
            return;
        }
        Flowable.interval(100, TimeUnit.MILLISECONDS)
                .filter(s -> this.ready)
                .take(1)
                .timeout(1, TimeUnit.DAYS)
                .blockingSubscribe();
    }

    @SneakyThrows
    public void init() {
        if (!this.ready) {
            synchronized (getClass()) {
                if (!this.ready) {
                    String id = EncryptDecryptConstant.getId();
                    if (!this.streamAll(EncryptDecryptEntity.class)
                            .where(s -> s.getId().equals(id))
                            .exists()) {

                        EncryptDecryptEntity encryptDecryptEntity = new EncryptDecryptEntity();
                        encryptDecryptEntity.setId(id);
                        encryptDecryptEntity.setCreateDate(new Date());
                        encryptDecryptEntity.setUpdateDate(new Date());

                        /**
                         * aes for common uses
                         */
                        encryptDecryptEntity.setSecretKeyOfAES(this.generateSecretKeyOfAES());

                        /**
                         * rsa for common uses
                         */
                        var keyPairOfRSA = this.generateKeyPairOfRSA();
                        encryptDecryptEntity.setPublicKeyOfRSA(keyPairOfRSA.getPublicKeyOfRSA());
                        encryptDecryptEntity.setPrivateKeyOfRSA(keyPairOfRSA.getPrivateKeyOfRSA());

                        this.persist(encryptDecryptEntity);
                    }
                    EncryptDecryptEntity encryptDecryptEntity = this.streamAll(EncryptDecryptEntity.class)
                            .where(s -> s.getId().equals(id))
                            .getOnlyValue();
                    this.keyOfRSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                            .generatePublic(new X509EncodedKeySpec(
                                    Base64.getDecoder().decode(encryptDecryptEntity.getPublicKeyOfRSA())));
                    this.keyOfRSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                            .generatePrivate(new PKCS8EncodedKeySpec(
                                    Base64.getDecoder().decode(encryptDecryptEntity.getPrivateKeyOfRSA())));
                    this.keyOfAESSecretKey = new SecretKeySpec(
                            Base64.getDecoder().decode(encryptDecryptEntity.getSecretKeyOfAES()), "AES");
                    this.ready = true;
                }
            }
        }
    }

}
