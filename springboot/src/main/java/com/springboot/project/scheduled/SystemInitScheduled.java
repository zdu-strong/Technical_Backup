package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.SystemDefaultRoleService;
import com.springboot.project.service.SystemRoleService;

@Component
public class SystemInitScheduled {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    @Autowired
    private SystemDefaultRoleService systemDefaultRoleService;

    @Autowired
    private SystemRoleService systemRoleService;

    private boolean hasInit = false;

    @Scheduled(initialDelay = 1000, fixedDelay = 24 * 60 * 60 * 1000)
    public void scheduled() {
        synchronized (this) {
            if (hasInit) {
                return;
            }
            this.initEncryptDecryptKey();
            this.initSystemRole();
            this.hasInit = true;
        }
    }

    private void initEncryptDecryptKey() {
        this.encryptDecryptService.getKeyOfAESSecretKey();
        this.encryptDecryptService.getKeyOfRSAPrivateKey();
        this.encryptDecryptService.getKeyOfRSAPublicKey();
    }

    private void initSystemRole() {
        while (true) {
            if (!systemDefaultRoleService.refresh()) {
                break;
            }
        }
        while (true) {
            if (!systemRoleService.refresh()) {
                break;
            }
        }
    }

}
