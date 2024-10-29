package com.springboot.project.scheduled;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.EmailUtil.AuthorizationEmailUtil;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.enumerate.LongTermTaskTypeEnum;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.UserRoleRelationModel;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.SystemDefaultRoleService;
import com.springboot.project.service.SystemRoleService;
import com.springboot.project.service.UserService;
import com.springboot.project.service.VerificationCodeEmailService;

import io.reactivex.rxjava3.core.Flowable;

@Component
public class SystemInitScheduled {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    @Autowired
    private SystemDefaultRoleService systemDefaultRoleService;

    @Autowired
    private SystemRoleService systemRoleService;

    @Autowired
    private LongTermTaskUtil longTermTaskUtil;

    @Autowired
    private GitProperties gitProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationEmailUtil authorizationEmailUtil;

    @Autowired
    private VerificationCodeEmailService verificationCodeEmailService;

    private boolean hasInit = false;

    @Scheduled(initialDelay = 0, fixedDelay = 24 * 60 * 60 * 1000)
    public void scheduled() {
        synchronized (this) {
            if (hasInit) {
                return;
            }
            this.init();
            this.hasInit = true;
        }
    }

    private void init() {
        this.initEncryptDecryptKey();
        this.initSystemRole();
        this.initSuperAdminUser();
    }

    private void initSuperAdminUser() {
        var email = "zdu.strong@gmail.com";
        var hasExists = !Flowable.fromCallable(() -> {
            return this.userService.getUserId(email);
        })
                .onErrorReturnItem("")
                .filter(s -> StringUtils.isNotBlank(s))
                .firstElement()
                .isEmpty()
                .blockingGet();
        if (hasExists) {
            return;
        }
        var superAdminUser = new UserModel();
        superAdminUser.setUsername("SuperAdmin");
        superAdminUser.setPassword(email);
        var verificationCodeEmailModel = this.authorizationEmailUtil.sendVerificationCode(email);
        verificationCodeEmailModel.setVerificationCode(
                this.verificationCodeEmailService.getById(verificationCodeEmailModel.getId()).getVerificationCode());
        superAdminUser.setUserEmailList(
                List.of(new UserEmailModel().setEmail(email).setVerificationCodeEmail(verificationCodeEmailModel)));
        superAdminUser.setOrganizeRoleRelationList(List.of());
        superAdminUser.setUserRoleRelationList(this.systemRoleService.getUserRoleListForSuperAdmin()
                .stream()
                .map(s -> new UserRoleRelationModel().setSystemRole(s))
                .toList());
        this.userService.create(superAdminUser);
    }

    private void initEncryptDecryptKey() {
        this.encryptDecryptService.getKeyOfAESSecretKey();
        this.encryptDecryptService.getKeyOfRSAPrivateKey();
        this.encryptDecryptService.getKeyOfRSAPublicKey();
    }

    private void initSystemRole() {
        this.longTermTaskUtil.run(() -> {
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
        }, null, new LongTermTaskUniqueKeyModel().setType(LongTermTaskTypeEnum.INIT_SYSTEM_ROLE.getType())
                .setUniqueKey(gitProperties.getCommitId()));
    }

}
