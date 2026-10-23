package com.john.project.scheduled;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.john.project.common.EncryptDecryptUtil.EncryptDecryptUtil;
import com.john.project.model.SuperAdminRoleQueryPaginationModel;
import com.john.project.properties.DevelopmentMockModeProperties;
import com.john.project.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.john.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.john.project.common.EmailUtil.AuthorizationEmailUtil;
import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.enums.LongTermTaskTypeEnum;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.model.UserEmailModel;
import com.john.project.model.UserModel;
import io.reactivex.rxjava3.core.Flowable;
import lombok.Getter;

@Component
public class SystemInitScheduled {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    @Autowired
    private PermissionService permissionService;

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

    @Autowired
    private DistributedExecutionUtil distributedExecutionUtil;

    @Autowired
    private EncryptDecryptUtil encryptDecryptUtil;

    @Autowired
    private UserRoleRelationService userRoleRelationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    protected DevelopmentMockModeProperties developmentMockModeProperties;

    @Getter
    private Boolean hasInit = false;

    @Scheduled(initialDelay = 0, fixedDelay = 24 * 60 * 60 * 1000)
    public void scheduled() {
        synchronized (getClass()) {
            if (hasInit) {
                return;
            }
            this.initEncryptDecryptKey();
            this.longTermTaskUtil.runSkipWhenExists(() -> {
                this.initUserRole();
                this.initSuperAdminUser();
            }, getLongTermTaskUniqueKeyModelForInitSystemData());
            this.distributedExecutionUtil.initializeInSystemInitScheduled();
            this.hasInit = true;
        }
    }

    private void initSuperAdminUser() {
        if (this.developmentMockModeProperties.getIsUnitTestEnvironment()) {
            return;
        }
        var email = "zdu.strong@gmail.com";
        var hasExists = !Flowable.fromCallable(() -> {
                    return this.userService.getUserId(email);
                })
                .onErrorReturnItem(StringUtils.EMPTY)
                .filter(s -> StringUtils.isNotBlank(s))
                .firstElement()
                .isEmpty()
                .blockingGet();
        if (hasExists) {
            return;
        }
        var superAdminUser = new UserModel();
        superAdminUser.setUsername("SuperAdmin");
        superAdminUser.setPassword(this.tokenService.getEncryptedPassword(email));
        var verificationCodeEmailModel = this.authorizationEmailUtil.sendVerificationCode(email);
        verificationCodeEmailModel.setVerificationCode(
                this.verificationCodeEmailService.getById(verificationCodeEmailModel.getId()).getVerificationCode());
        superAdminUser.setUserEmailList(
                List.of(new UserEmailModel().setEmail(email).setVerificationCodeEmail(verificationCodeEmailModel)));
        var superAdminRoleQueryPaginationModel = new SuperAdminRoleQueryPaginationModel();
        superAdminRoleQueryPaginationModel.setPageNum(1L);
        superAdminRoleQueryPaginationModel.setPageSize((long) SystemRoleEnum.values().length);
        superAdminUser.setRoleList(
                this.roleService.searchRoleForSuperAdminByPagination(superAdminRoleQueryPaginationModel).getItems());
        this.userService.create(superAdminUser);
    }

    private void initEncryptDecryptKey() {
        Flowable.just("")
                .doOnNext(s -> {
                    while (!this.encryptDecryptService.init()) {
                    }
                })
                .retry((s) -> {
                    ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
                    return true;
                })
                .timeout(1, TimeUnit.DAYS)
                .blockingSubscribe();
        this.encryptDecryptUtil.getKeyOfSecretKeyOfAES();
        this.encryptDecryptUtil.getKeyOfPublicKeyOfMLKEM();
        this.encryptDecryptUtil.getKeyOfPrivateKeyOfMLKEM();
    }

    private void initUserRole() {
        while (true) {
            if (!this.permissionService.refresh()) {
                break;
            }
        }
        while (true) {
            if (!this.userRoleRelationService.refresh()) {
                break;
            }
        }
    }

    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelForInitSystemData() {
        var uniqueKeyModel = new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.INIT_SYSTEM_DATABASE_DATA.getValue())
                .setUniqueKey(gitProperties.getCommitId());
        return uniqueKeyModel;
    }

}
