package com.springboot.project.service;

import java.nio.charset.StandardCharsets;
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
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.EncryptDecryptEntity;
import com.springboot.project.enumerate.EncryptDecryptEnum;
import com.springboot.project.model.EncryptDecryptModel;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;

@Service
public class EncryptDecryptService extends BaseService {

    private RSAPublicKey keyOfRSAPublicKey;
    private RSAPrivateKey keyOfRSAPrivateKey;
    private SecretKey keyOfAESSecretKey;
    private Boolean ready = false;

    public String encryptByAES(String text) {
        var secretKeyOfAES = Base64.getEncoder().encodeToString(this.getKeyOfAESSecretKey().getEncoded());
        return this.encryptByAES(text, secretKeyOfAES);
    }

    public String decryptByAES(String text) {
        var secretKeyOfAES = Base64.getEncoder().encodeToString(this.getKeyOfAESSecretKey().getEncoded());
        return this.decryptByAES(text, secretKeyOfAES);
    }

    public String encryptByPrivateKeyOfRSA(String text) {
        var rsa = new RSA(this.getKeyOfRSAPrivateKey(), this.getKeyOfRSAPublicKey());
        return rsa.encryptBase64(text, KeyType.PrivateKey);
    }

    public String encryptByPublicKeyOfRSA(String text) {
        var rsa = new RSA(this.getKeyOfRSAPrivateKey(), this.getKeyOfRSAPublicKey());
        return rsa.encryptBase64(text, KeyType.PublicKey);
    }

    public String decryptByByPublicKeyOfRSA(String text) {
        var rsa = new RSA(this.getKeyOfRSAPrivateKey(), this.getKeyOfRSAPublicKey());
        return rsa.decryptStr(text, KeyType.PublicKey);
    }

    public String decryptByByPrivateKeyOfRSA(String text) {
        var rsa = new RSA(this.getKeyOfRSAPrivateKey(), this.getKeyOfRSAPublicKey());
        return rsa.decryptStr(text, KeyType.PrivateKey);
    }

    public String encryptByAES(String text, String secretKeyOfAES) {
        var salt = Base64.getEncoder()
                .encodeToString(DigestUtils.md5((Generators.timeBasedReorderedGenerator().generate().toString()
                        + Generators.randomBasedGenerator().generate().toString()).getBytes(StandardCharsets.UTF_8)));
        var aes = new AES(Mode.CBC, Padding.PKCS5Padding, new SecretKeySpec(
                Base64.getDecoder().decode(secretKeyOfAES), "AES"),
                Base64.getDecoder().decode(salt));
        return salt + aes.encryptBase64(text);
    }

    public String decryptByAES(String text, String secretKeyOfAES) {
        var salt = text.substring(0, 24);
        text = text.substring(24);
        var aes = new AES(Mode.CBC, Padding.PKCS5Padding, new SecretKeySpec(
                Base64.getDecoder().decode(secretKeyOfAES), "AES"),
                Base64.getDecoder().decode(salt));
        return aes.decryptStr(text);
    }

    public String encryptByPrivateKeyOfRSA(String text, String privateKeyOfRSA) {
        try {
            var keyOfRSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(
                            Base64.getDecoder().decode(privateKeyOfRSA)));
            var rsa = new RSA(keyOfRSAPrivateKey, null);
            return rsa.encryptBase64(text, KeyType.PrivateKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String encryptByPublicKeyOfRSA(String text, String publicKeyOfRSA) {
        try {
            var keyOfRSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(
                            Base64.getDecoder().decode(publicKeyOfRSA)));
            var rsa = new RSA(null, keyOfRSAPublicKey);
            return rsa.encryptBase64(text, KeyType.PublicKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String decryptByByPublicKeyOfRSA(String text, String publicKeyOfRSA) {
        try {
            var keyOfRSAPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(
                            Base64.getDecoder().decode(publicKeyOfRSA)));
            var rsa = new RSA(null, keyOfRSAPublicKey);
            return rsa.decryptStr(text, KeyType.PublicKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String decryptByByPrivateKeyOfRSA(String text, String privateKeyOfRSA) {
        try {
            var keyOfRSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(
                            Base64.getDecoder().decode(privateKeyOfRSA)));
            var rsa = new RSA(keyOfRSAPrivateKey, null);
            return rsa.decryptStr(text, KeyType.PrivateKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String generateSecretKeyOfAES(String password) {
        try {
            var salt = DigestUtils.md5(password);
            var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            var spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            var secret = new SecretKeySpec(factory.generateSecret(spec)
                    .getEncoded(), "AES");
            return Base64.getEncoder().encodeToString(secret.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String generateSecretKeyOfAES() {
        try {
            var keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public EncryptDecryptModel generateKeyPairOfRSA() {
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            var keyPair = keyPairGenerator.generateKeyPair();
            return new EncryptDecryptModel()
                    .setPublicKeyOfRSA(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()))
                    .setPrivateKeyOfRSA(
                            Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String encryptWithFixedSaltByAES(String text) {
        var salt = Base64.getEncoder()
                .encodeToString(
                        DigestUtils.md5(this
                                .generateSecretKeyOfAES(Base64.getEncoder()
                                        .encodeToString(this.getKeyOfAESSecretKey().getEncoded())
                                        + Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)))
                                .getBytes(StandardCharsets.UTF_8)));
        var aes = new AES(Mode.CBC, Padding.PKCS5Padding, this.getKeyOfAESSecretKey(),
                Base64.getDecoder().decode(salt));
        return salt + aes.encryptBase64(text);
    }

    public RSAPrivateKey getKeyOfRSAPrivateKey() {
        this.initKey();
        return this.keyOfRSAPrivateKey;
    }

    public RSAPublicKey getKeyOfRSAPublicKey() {
        this.initKey();
        return this.keyOfRSAPublicKey;
    }

    public SecretKey getKeyOfAESSecretKey() {
        this.initKey();
        return this.keyOfAESSecretKey;
    }

    private void initKey() {
        try {
            if (!this.ready) {
                synchronized (getClass()) {
                    if (!this.ready) {
                        String id = EncryptDecryptEnum.getId();
                        if (!this.EncryptDecryptEntity().where(s -> s.getId().equals(id)).exists()) {

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
                        EncryptDecryptEntity encryptDecryptEntity = this.EncryptDecryptEntity()
                                .where(s -> s.getId().equals(id)).getOnlyValue();
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
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
