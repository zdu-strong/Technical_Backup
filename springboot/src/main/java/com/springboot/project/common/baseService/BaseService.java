package com.springboot.project.common.baseService;

import com.google.cloud.spanner.AbortedDueToConcurrentModificationException;
import io.grpc.StatusRuntimeException;
import org.hibernate.exception.GenericJDBCException;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.google.cloud.spanner.AbortedException;
import com.springboot.project.common.TimeZoneUtil.TimeZoneUtil;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.properties.DateFormatProperties;
import com.springboot.project.properties.HibernateDialectProperties;
import com.springboot.project.properties.IsDevelopmentMockModeProperties;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.entity.UserMessageDeactivateEntity;
import com.springboot.project.entity.DistributedExecutionEntity;
import com.springboot.project.entity.DistributedExecutionTaskEntity;
import com.springboot.project.entity.EncryptDecryptEntity;
import com.springboot.project.entity.FriendshipEntity;
import com.springboot.project.entity.LoggerEntity;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.entity.OrganizeRelationEntity;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.entity.SystemDefaultRoleEntity;
import com.springboot.project.entity.SystemRoleEntity;
import com.springboot.project.entity.SystemRoleRelationEntity;
import com.springboot.project.entity.TokenEntity;
import com.springboot.project.entity.UserBlackOrganizeClosureEntity;
import com.springboot.project.entity.UserBlackOrganizeEntity;
import com.springboot.project.entity.UserEmailEntity;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.entity.VerificationCodeEmailEntity;
import com.springboot.project.format.DistributedExecutionFormatter;
import com.springboot.project.format.DistributedExecutionTaskFormatter;
import com.springboot.project.format.FriendshipFormatter;
import com.springboot.project.format.LoggerFormatter;
import com.springboot.project.format.LongTermTaskFormatter;
import com.springboot.project.format.OrganizeFormatter;
import com.springboot.project.format.StorageSpaceFormatter;
import com.springboot.project.format.SystemDefaultRoleFormatter;
import com.springboot.project.format.SystemRoleFormatter;
import com.springboot.project.format.TokenFormatter;
import com.springboot.project.format.UserBlackOrganizeFormatter;
import com.springboot.project.format.UserEmailFormatter;
import com.springboot.project.format.UserFormatter;
import com.springboot.project.format.UserMessageFormatter;
import com.springboot.project.format.UserRoleRelationFormatter;
import com.springboot.project.format.VerificationCodeEmailFormatter;
import com.springboot.project.service.DistributedExecutionService;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.SystemRoleRelationService;
import com.springboot.project.service.SystemRoleService;
import com.springboot.project.service.UserEmailService;
import com.springboot.project.service.UserMessageDeactivateService;
import com.springboot.project.service.UserRoleRelationService;
import com.springboot.project.service.UserService;
import com.springboot.project.service.VerificationCodeEmailService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.springboot.project.entity.UserSystemRoleRelationEntity;

@Service
@Transactional(rollbackFor = Throwable.class)
@Retryable(maxAttempts = 10, retryFor = {
        GenericJDBCException.class,
        ObjectOptimisticLockingFailureException.class,
        AbortedException.class,
        CannotAcquireLockException.class,
        CannotCreateTransactionException.class,
        DataAccessResourceFailureException.class,
        IllegalStateException.class,
        StatusRuntimeException.class,
        AbortedDueToConcurrentModificationException.class,
})
public abstract class BaseService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    protected Storage storage;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PermissionUtil permissionUtil;

    @Autowired
    protected TimeZoneUtil timeZoneUtil;

    @Autowired
    protected DateFormatProperties dateFormatProperties;

    @Autowired
    private HibernateDialectProperties HibernateDialectProperties;

    @Autowired
    protected IsDevelopmentMockModeProperties isDevelopmentMockModeProperties;

    @Autowired
    protected OrganizeService organizeService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserEmailService userEmailService;

    @Autowired
    protected EncryptDecryptService encryptDecryptService;

    @Autowired
    protected VerificationCodeEmailService verificationCodeEmailService;

    @Autowired
    protected UserMessageDeactivateService userMessageDeactivateService;

    @Autowired
    protected SystemRoleRelationService systemRoleRelationService;

    @Autowired
    protected DistributedExecutionService distributedExecutionService;

    @Autowired
    protected SystemRoleService systemRoleService;

    @Autowired
    protected UserRoleRelationService userRoleRelationService;

    @Autowired
    protected TokenFormatter tokenFormatter;

    @Autowired
    protected StorageSpaceFormatter storageSpaceFormatter;

    @Autowired
    protected UserEmailFormatter userEmailFormatter;

    @Autowired
    protected UserFormatter userFormatter;

    @Autowired
    protected LongTermTaskFormatter longTermTaskFormatter;

    @Autowired
    protected OrganizeFormatter organizeFormatter;

    @Autowired
    protected UserMessageFormatter userMessageFormatter;

    @Autowired
    protected FriendshipFormatter friendshipFormatter;

    @Autowired
    protected LoggerFormatter loggerFormatter;

    @Autowired
    protected VerificationCodeEmailFormatter verificationCodeEmailFormatter;

    @Autowired
    protected DistributedExecutionFormatter distributedExecutionFormatter;

    @Autowired
    protected UserBlackOrganizeFormatter userBlackOrganizeFormatter;

    @Autowired
    protected SystemRoleFormatter systemRoleFormatter;

    @Autowired
    protected SystemDefaultRoleFormatter systemDefaultRoleFormatter;

    @Autowired
    protected DistributedExecutionTaskFormatter distributedExecutionTaskFormatter;

    @Autowired
    protected UserRoleRelationFormatter userRoleRelationFormatter;

    protected void persist(Object entity) {
        this.entityManager.persist(entity);
    }

    protected void merge(Object entity) {
        this.entityManager.merge(entity);
    }

    protected void remove(Object entity) {
        this.entityManager.remove(entity);
    }

    protected JPAJinqStream<StorageSpaceEntity> StorageSpaceEntity() {
        return this.streamAll(StorageSpaceEntity.class);
    }

    protected JPAJinqStream<EncryptDecryptEntity> EncryptDecryptEntity() {
        return this.streamAll(EncryptDecryptEntity.class);
    }

    protected JPAJinqStream<UserEmailEntity> UserEmailEntity() {
        return this.streamAll(UserEmailEntity.class);
    }

    protected JPAJinqStream<UserEntity> UserEntity() {
        return this.streamAll(UserEntity.class);
    }

    protected JPAJinqStream<LongTermTaskEntity> LongTermTaskEntity() {
        return this.streamAll(LongTermTaskEntity.class);
    }

    protected JPAJinqStream<OrganizeEntity> OrganizeEntity() {
        return this.streamAll(OrganizeEntity.class);
    }

    protected JPAJinqStream<UserMessageEntity> UserMessageEntity() {
        return this.streamAll(UserMessageEntity.class);
    }

    protected JPAJinqStream<TokenEntity> TokenEntity() {
        return this.streamAll(TokenEntity.class);
    }

    protected JPAJinqStream<FriendshipEntity> FriendshipEntity() {
        return this.streamAll(FriendshipEntity.class);
    }

    protected JPAJinqStream<LoggerEntity> LoggerEntity() {
        return this.streamAll(LoggerEntity.class);
    }

    protected JPAJinqStream<VerificationCodeEmailEntity> VerificationCodeEmailEntity() {
        return this.streamAll(VerificationCodeEmailEntity.class);
    }

    protected JPAJinqStream<DistributedExecutionEntity> DistributedExecutionEntity() {
        return this.streamAll(DistributedExecutionEntity.class);
    }

    protected JPAJinqStream<OrganizeRelationEntity> OrganizeRelationEntity() {
        return this.streamAll(OrganizeRelationEntity.class);
    }

    protected JPAJinqStream<UserBlackOrganizeEntity> UserBlackOrganizeEntity() {
        return this.streamAll(UserBlackOrganizeEntity.class);
    }

    protected JPAJinqStream<UserBlackOrganizeClosureEntity> UserBlackOrganizeClosureEntity() {
        return this.streamAll(UserBlackOrganizeClosureEntity.class);
    }

    protected JPAJinqStream<UserMessageDeactivateEntity> UserMessageDeactivateEntity() {
        return this.streamAll(UserMessageDeactivateEntity.class);
    }

    protected JPAJinqStream<SystemDefaultRoleEntity> SystemDefaultRoleEntity() {
        return this.streamAll(SystemDefaultRoleEntity.class);
    }

    protected JPAJinqStream<SystemRoleEntity> SystemRoleEntity() {
        return this.streamAll(SystemRoleEntity.class);
    }

    protected JPAJinqStream<SystemRoleRelationEntity> SystemRoleRelationEntity() {
        return this.streamAll(SystemRoleRelationEntity.class);
    }

    protected JPAJinqStream<UserSystemRoleRelationEntity> UserSystemRoleRelationEntity() {
        return this.streamAll(UserSystemRoleRelationEntity.class);
    }

    protected JPAJinqStream<DistributedExecutionTaskEntity> DistributedExecutionTaskEntity() {
        return this.streamAll(DistributedExecutionTaskEntity.class);
    }

    private <U> JPAJinqStream<U> streamAll(Class<U> entity) {
        var jinqJPAStreamProvider = new JinqJPAStreamProvider(
                entityManager.getMetamodel());
        JPQLFunction.registerCustomSqlFunction(jinqJPAStreamProvider);
        jinqJPAStreamProvider.setHint("exceptionOnTranslationFail", true);
        return jinqJPAStreamProvider.streamAll(entityManager, entity);
    }

    protected String newId() {
        if (!this.HibernateDialectProperties.getIsNewSqlDatabase()) {
            return Generators.timeBasedReorderedGenerator().generate().toString();
        } else {
            return new StringBuilder(Generators.timeBasedReorderedGenerator().generate().toString()).reverse()
                    .toString();
        }
    }

}