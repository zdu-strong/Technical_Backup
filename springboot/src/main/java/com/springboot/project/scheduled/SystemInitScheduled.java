package com.springboot.project.scheduled;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.springboot.project.common.EmailUtil.AuthorizationEmailUtil;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.enums.LongTermTaskTypeEnum;
import com.springboot.project.enums.SystemRoleEnum;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.LongTermTaskService;
import com.springboot.project.service.PermissionService;
import com.springboot.project.service.RoleService;
import com.springboot.project.service.UserService;
import com.springboot.project.service.VerificationCodeEmailService;
import io.reactivex.rxjava3.core.Flowable;
import lombok.Getter;

@Component
public class SystemInitScheduled {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

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
    private LongTermTaskService longTermTaskService;

    @Autowired
    private DistributedExecutionUtil distributedExecutionUtil;

    @Getter
    private Boolean hasInit = false;

    @Scheduled(initialDelay = 0, fixedDelay = 24 * 60 * 60 * 1000)
    public void scheduled() {
        synchronized (this) {
            if (hasInit) {
                return;
            }
            initDistributedExecution();
            var uniqueKeyModel = new LongTermTaskUniqueKeyModel()
                    .setType(LongTermTaskTypeEnum.INIT_SYSTEM_DATABASE_DATA.getValue())
                    .setUniqueKey(gitProperties.getCommitId());
            if (this.longTermTaskService.findOneNotRunning(List.of(uniqueKeyModel)) != null) {
                this.longTermTaskUtil.runSkipWhenExists(() -> {
                    this.init();
                }, uniqueKeyModel);
            }
            this.hasInit = true;
        }
    }

    private void init() {
        this.initEncryptDecryptKey();
        this.initUserRole();
        this.initSuperAdminUser();
    }

    private void initSuperAdminUser() {
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
        superAdminUser.setPassword(email);
        var verificationCodeEmailModel = this.authorizationEmailUtil.sendVerificationCode(email);
        verificationCodeEmailModel.setVerificationCode(
                this.verificationCodeEmailService.getById(verificationCodeEmailModel.getId()).getVerificationCode());
        superAdminUser.setUserEmailList(
                List.of(new UserEmailModel().setEmail(email).setVerificationCodeEmail(verificationCodeEmailModel)));
        superAdminUser.setRoleList(
                this.roleService.searchUserRoleForSuperAdminByPagination(1, SystemRoleEnum.values().length).getList());
        this.userService.create(superAdminUser);
    }

    private void initEncryptDecryptKey() {
        this.encryptDecryptService.init();
        this.encryptDecryptService.getKeyOfAESSecretKey();
        this.encryptDecryptService.getKeyOfRSAPrivateKey();
        this.encryptDecryptService.getKeyOfRSAPublicKey();
    }

    private void initUserRole() {
        while (true) {
            if (!permissionService.refresh()) {
                break;
            }
        }
        while (true) {
            if (!roleService.refresh()) {
                break;
            }
        }
    }

    private void initDistributedExecution() {
        for (var distributedExecutionEnum : DistributedExecutionEnum.values()) {
            Flowable.timer(distributedExecutionEnum.getTheIntervalBetweenTwoExecutions().toMillis(),
                    TimeUnit.MILLISECONDS)
                    .doOnNext(s -> {
                        this.distributedExecutionUtil
                                .refreshData(distributedExecutionEnum);
                    })
                    .repeat()
                    .retry()
                    .subscribe();
        }
    }

}
