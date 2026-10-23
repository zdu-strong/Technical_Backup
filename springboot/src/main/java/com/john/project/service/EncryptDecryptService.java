package com.john.project.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.bouncycastle.crypto.generators.MLKEMKeyPairGenerator;
import org.bouncycastle.crypto.kems.MLKEMExtractor;
import org.bouncycastle.crypto.kems.MLKEMGenerator;
import org.bouncycastle.crypto.params.MLKEMKeyGenerationParameters;
import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.EncryptDecryptEntity;
import com.john.project.model.EncryptDecryptModel;
import lombok.SneakyThrows;

@Service
public class EncryptDecryptService extends BaseService {

    private EncryptDecryptModel encryptDecryptModel;

    @Transactional(readOnly = true)
    public String encryptByAES(String text) {
        var secretKeyOfAES = this.getKeyOfSecretKeyOfAES();
        return this.encryptByAES(text, secretKeyOfAES);
    }

    @Transactional(readOnly = true)
    public String decryptByAES(String text) {
        var secretKeyOfAES = this.getKeyOfSecretKeyOfAES();
        return this.decryptByAES(text, secretKeyOfAES);
    }

    @Transactional(readOnly = true)
    public String encryptByPublicKeyOfMLKEM(String text) {
        var publicKeyOfMLKEM = this.getKeyOfPublicKeyOfMLKEM();
        return this.encryptByPublicKeyOfMLKEM(text, publicKeyOfMLKEM);
    }

    @Transactional(readOnly = true)
    public String decryptByByPrivateKeyOfMLKEM(String text) {
        var privateKeyOfMLKEM = this.getKeyOfPrivateKeyOfMLKEM();
        return this.decryptByByPrivateKeyOfMLKEM(text, privateKeyOfMLKEM);
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public String encryptByAES(String text, String secretKeyOfAES) {
        var salt = RandomUtil.randomBytes(16);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(
                HexUtil.decodeHex(secretKeyOfAES), "AES"), new GCMParameterSpec(16 * 8, salt));
        return HexUtil.encodeHexStr(ArrayUtils.addAll(salt, cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @SneakyThrows
    public String encryptWithFixedSaltByAES(String text) {
        var salt = DigestUtils.md5(text);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(
                HexUtil.decodeHex(this.getKeyOfSecretKeyOfAES()), "AES"), new GCMParameterSpec(16 * 8, salt));
        return HexUtil.encodeHexStr(ArrayUtils.addAll(salt, cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
    }

    @SneakyThrows
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public String generateSecretKeyOfAES() {
        var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return HexUtil.encodeHexStr(keyGenerator.generateKey().getEncoded());
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public String generateSecretKeyOfAES(String password) {
        var salt = DigestUtils.md5(password);
        var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        var spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        var secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return HexUtil.encodeHexStr(secret.getEncoded());
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public String getKeyOfPublicKeyOfMLKEM() {
        this.initKey();
        return this.encryptDecryptModel.getPublicKeyOfMLKEM();
    }

    @Transactional(readOnly = true)
    public String getKeyOfPrivateKeyOfMLKEM() {
        this.initKey();
        return this.encryptDecryptModel.getPrivateKeyOfMLKEM();
    }

    @Transactional(readOnly = true)
    public String getKeyOfSecretKeyOfAES() {
        this.initKey();
        return this.encryptDecryptModel.getSecretKeyOfAES();
    }

    @SneakyThrows
    private void initKey() {
        while (this.encryptDecryptModel == null) {
            ThreadUtils.sleepQuietly(Duration.ofMillis(100));
        }
    }

    @SneakyThrows
    public boolean init() {
        var name = getClass().getSimpleName();

        if (this.encryptDecryptModel == null) {
            if (this.streamAll(EncryptDecryptEntity.class)
                    .where(s -> s.getName().equals(name))
                    .exists()) {
                var encryptDecryptEntity = this.streamAll(EncryptDecryptEntity.class)
                        .where(s -> s.getName().equals(name))
                        .getOnlyValue();
                this.encryptDecryptModel = this.encryptDecryptFormatter.format(encryptDecryptEntity);
            } else {
                var encryptDecryptEntity = new EncryptDecryptEntity();
                encryptDecryptEntity.setId(newId());
                encryptDecryptEntity.setName(name);
                encryptDecryptEntity.setCreateDate(new Date());
                encryptDecryptEntity.setUpdateDate(new Date());

                /*
                 * aes for common uses
                 */
                encryptDecryptEntity.setSecretKeyOfAES(this.generateSecretKeyOfAES());

                /*
                 * ml-kem for common uses
                 */
                var keyPairOfMLKEM = this.generateKeyPairOfMLKEM();
                encryptDecryptEntity.setPublicKeyOfMLKEM(keyPairOfMLKEM.getPublicKeyOfMLKEM());
                encryptDecryptEntity.setPrivateKeyOfMLKEM(keyPairOfMLKEM.getPrivateKeyOfMLKEM());

                this.persist(encryptDecryptEntity);
            }
        }

        return this.encryptDecryptModel != null;
    }

}
