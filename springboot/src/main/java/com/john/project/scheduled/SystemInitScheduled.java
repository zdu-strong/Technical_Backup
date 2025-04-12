package com.john.project.scheduled;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.john.project.model.SuperAdminUserRoleQueryPaginationModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.john.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.john.project.common.EmailUtil.AuthorizationEmailUtil;
import com.john.project.common.longtermtask.LongTermTaskUtil;
import com.john.project.enums.DistributedExecutionEnum;
import com.john.project.enums.LongTermTaskTypeEnum;
import com.john.project.enums.SystemRoleEnum;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.model.UserEmailModel;
import com.john.project.model.UserModel;
import com.john.project.service.EncryptDecryptService;
import com.john.project.service.LongTermTaskService;
import com.john.project.service.PermissionService;
import com.john.project.service.UserRoleRelationService;
import com.john.project.service.UserService;
import com.john.project.service.VerificationCodeEmailService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
    private LongTermTaskService longTermTaskService;

    @Autowired
    private DistributedExecutionUtil distributedExecutionUtil;

    @Autowired
    private UserRoleRelationService userRoleRelationService;

    @Getter
    private Boolean hasInit = false;

    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

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
        var superAdminUserRoleQueryPaginationModel = new SuperAdminUserRoleQueryPaginationModel();
        superAdminUserRoleQueryPaginationModel.setPageNum(1L);
        superAdminUserRoleQueryPaginationModel.setPageSize((long) SystemRoleEnum.values().length);
        superAdminUser.setRoleList(
                this.userRoleRelationService.searchUserRoleForSuperAdminByPagination(superAdminUserRoleQueryPaginationModel).getItems());
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

    private void initDistributedExecution() {
        for (var distributedExecutionEnum : DistributedExecutionEnum.values()) {
            Flowable.timer(distributedExecutionEnum.getTheIntervalBetweenTwoExecutions().toMillis(),
                            TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.from(executor))
                    .observeOn(Schedulers.from(executor))
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
