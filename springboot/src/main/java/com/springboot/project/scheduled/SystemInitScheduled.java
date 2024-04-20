package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.project.service.EncryptDecryptService;

@Component
public class SystemInitScheduled {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    @Scheduled(initialDelay = 1000, fixedDelay = 24 * 60 * 60 * 1000)
    public void scheduled() {
        this.initEncryptDecryptKey();
    }

    public void initEncryptDecryptKey() {
        this.encryptDecryptService.getKeyOfAESSecretKey();
        this.encryptDecryptService.getKeyOfRSAPrivateKey();
        this.encryptDecryptService.getKeyOfRSAPublicKey();
    }

}
