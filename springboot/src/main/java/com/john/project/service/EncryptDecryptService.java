package com.john.project.service;

import java.time.Duration;
import java.util.Date;

import org.apache.commons.lang3.ThreadUtils;
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
    public EncryptDecryptModel getEncryptDecryptModel() {
        while (this.encryptDecryptModel == null) {
            ThreadUtils.sleepQuietly(Duration.ofMillis(100));
        }
        return this.encryptDecryptModel;
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
                encryptDecryptEntity.setSecretKeyOfAES(this.encryptDecryptUtil.generateSecretKeyOfAES());

                /*
                 * ml-kem for common uses
                 */
                var keyPairOfMLKEM = this.encryptDecryptUtil.generateKeyPairOfMLKEM();
                encryptDecryptEntity.setPublicKeyOfMLKEM(keyPairOfMLKEM.getPublicKeyOfMLKEM());
                encryptDecryptEntity.setPrivateKeyOfMLKEM(keyPairOfMLKEM.getPrivateKeyOfMLKEM());

                this.persist(encryptDecryptEntity);
            }
        }

        return this.encryptDecryptModel != null;
    }

}
