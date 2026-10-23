package com.john.project.common.EncryptDecryptUtil;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import com.john.project.model.EncryptDecryptModel;
import com.john.project.service.EncryptDecryptService;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.crypto.generators.MLKEMKeyPairGenerator;
import org.bouncycastle.crypto.kems.MLKEMExtractor;
import org.bouncycastle.crypto.kems.MLKEMGenerator;
import org.bouncycastle.crypto.params.MLKEMKeyGenerationParameters;
import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Component
public class EncryptDecryptUtil {

    @Autowired
    @Lazy
    private EncryptDecryptService encryptDecryptService;

    private EncryptDecryptModel encryptDecryptModel;

    public String encryptByAES(String text) {
        var secretKeyOfAES = this.getKeyOfSecretKeyOfAES();
        return this.encryptByAES(text, secretKeyOfAES);
    }

    public String decryptByAES(String text) {
        var secretKeyOfAES = this.getKeyOfSecretKeyOfAES();
        return this.decryptByAES(text, secretKeyOfAES);
    }

    public String encryptByPublicKeyOfMLKEM(String text) {
        var publicKeyOfMLKEM = this.getKeyOfPublicKeyOfMLKEM();
        return this.encryptByPublicKeyOfMLKEM(text, publicKeyOfMLKEM);
    }

    public String decryptByByPrivateKeyOfMLKEM(String text) {
        var privateKeyOfMLKEM = this.getKeyOfPrivateKeyOfMLKEM();
        return this.decryptByByPrivateKeyOfMLKEM(text, privateKeyOfMLKEM);
    }

    @SneakyThrows
    public String encryptByAES(String text, String secretKeyOfAES) {
        var salt = RandomUtil.randomBytes(16);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(
                HexUtil.decodeHex(secretKeyOfAES), "AES"), new GCMParameterSpec(16 * 8, salt));
        return HexUtil.encodeHexStr(ArrayUtils.addAll(salt, cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
    }

    @SneakyThrows
    public String decryptByAES(String text, String secretKeyOfAES) {
        var textByteList = HexUtil.decodeHex(text);
        var salt = ArrayUtils.subarray(textByteList, 0, 16);
        var encryptedTextByteList = ArrayUtils.subarray(textByteList, 16, textByteList.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(
                HexUtil.decodeHex(secretKeyOfAES), "AES"), new GCMParameterSpec(16 * 8, salt));
        return new String(cipher.doFinal(encryptedTextByteList), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public String encryptWithFixedSaltByAES(String text) {
        var secretKeyOfAES = new SecretKeySpec(HexUtil.decodeHex(this.getKeyOfSecretKeyOfAES()), "AES");
        var salt = DigestUtils.md5(text);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeyOfAES, new GCMParameterSpec(16 * 8, salt));
        return HexUtil.encodeHexStr(ArrayUtils.addAll(salt, cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
    }

    @SneakyThrows
    public String encryptByPublicKeyOfMLKEM(String text, String publicKeyOfMLKEM) {
        var generatorOfMLKEM = new MLKEMGenerator(SecureRandom.getInstanceStrong());
        var keyOfPublicKeyOfMLKEM = new MLKEMPublicKeyParameters(MLKEMParameters.ml_kem_1024, HexUtil.decodeHex(publicKeyOfMLKEM));
        var encapsulatedOfMLKEM = generatorOfMLKEM.generateEncapsulated(keyOfPublicKeyOfMLKEM);
        var sharedSecretKeyOfAES = HexUtil.encodeHexStr(encapsulatedOfMLKEM.getSecret());
        var encapsulateCiphertextByteList = encapsulatedOfMLKEM.getEncapsulation();
        var resultOfEncryptByAES = this.encryptByAES(text, sharedSecretKeyOfAES);
        return HexUtil.encodeHexStr(ArrayUtils.addAll(encapsulateCiphertextByteList, HexUtil.decodeHex(resultOfEncryptByAES)));
    }

    @SneakyThrows
    public String decryptByByPrivateKeyOfMLKEM(String text, String privateKeyOfMLKEM) {
        var textByteList = HexUtil.decodeHex(text);
        var encapsulateCipherByteList = ArrayUtils.subarray(textByteList, 0, 1568);
        var encryptedText = HexUtil.encodeHexStr(ArrayUtils.subarray(textByteList, 1568, textByteList.length));
        var keyOfPrivateKeyOfMLKEM = new MLKEMPrivateKeyParameters(MLKEMParameters.ml_kem_1024, HexUtil.decodeHex(privateKeyOfMLKEM));
        var extractorOfMLKEM = new MLKEMExtractor(keyOfPrivateKeyOfMLKEM);
        var sharedSecretKeyOfAES = HexUtil.encodeHexStr(extractorOfMLKEM.extractSecret(encapsulateCipherByteList));
        return this.decryptByAES(encryptedText, sharedSecretKeyOfAES);
    }

    @SneakyThrows
    public String generateSecretKeyOfAES() {
        var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return HexUtil.encodeHexStr(keyGenerator.generateKey().getEncoded());
    }

    @SneakyThrows
    public String generateSecretKeyOfAES(String password) {
        var salt = DigestUtils.md5(password);
        var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        var spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        var secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return HexUtil.encodeHexStr(secret.getEncoded());
    }

    @SneakyThrows
    public EncryptDecryptModel generateKeyPairOfMLKEM() {
        var keyPairGeneratorOfMLKEM = new MLKEMKeyPairGenerator();
        keyPairGeneratorOfMLKEM.init(new MLKEMKeyGenerationParameters(SecureRandom.getInstanceStrong(), MLKEMParameters.ml_kem_1024));
        var keyPairOfMLKEM = keyPairGeneratorOfMLKEM.generateKeyPair();
        var publicKeyOfMLKEM = (MLKEMPublicKeyParameters) keyPairOfMLKEM.getPublic();
        var privateKeyOfMLKEM = (MLKEMPrivateKeyParameters) keyPairOfMLKEM.getPrivate();
        return new EncryptDecryptModel()
                .setPublicKeyOfMLKEM(HexUtil.encodeHexStr(publicKeyOfMLKEM.getEncoded()))
                .setPrivateKeyOfMLKEM(HexUtil.encodeHexStr(privateKeyOfMLKEM.getEncoded()));
    }

    public String getKeyOfPublicKeyOfMLKEM() {
        this.initKey();
        return this.encryptDecryptModel.getPublicKeyOfMLKEM();
    }

    public String getKeyOfPrivateKeyOfMLKEM() {
        this.initKey();
        return this.encryptDecryptModel.getPrivateKeyOfMLKEM();
    }

    public String getKeyOfSecretKeyOfAES() {
        this.initKey();
        return this.encryptDecryptModel.getSecretKeyOfAES();
    }

    private void initKey() {
        if (this.encryptDecryptModel == null) {
            this.encryptDecryptModel = this.encryptDecryptService.getEncryptDecryptModel();
        }
    }

}

