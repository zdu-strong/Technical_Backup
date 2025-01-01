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
import com.springboot.project.format.DistributedExecutionDetailFormatter;
import com.springboot.project.format.DistributedExecutionMainFormatter;
import com.springboot.project.format.FriendshipFormatter;
import com.springboot.project.format.LoggerFormatter;
import com.springboot.project.format.LongTermTaskFormatter;
import com.springboot.project.format.OrganizeFormatter;
import com.springboot.project.format.StorageSpaceFormatter;
import com.springboot.project.format.PermissionFormatter;
import com.springboot.project.format.RoleFormatter;
import com.springboot.project.format.TokenFormatter;
import com.springboot.project.format.UserBlackOrganizeFormatter;
import com.springboot.project.format.UserEmailFormatter;
import com.springboot.project.format.UserFormatter;
import com.springboot.project.format.UserMessageFormatter;
import com.springboot.project.format.UserRoleRelationFormatter;
import com.springboot.project.format.VerificationCodeEmailFormatter;
import com.springboot.project.service.DistributedExecutionMainService;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.OrganizeService;
import com.springboot.project.service.RolePermissionRelationService;
import com.springboot.project.service.RoleService;
import com.springboot.project.service.UserEmailService;
import com.springboot.project.service.UserMessageDeactivateService;
import com.springboot.project.service.UserRoleRelationService;
import com.springboot.project.service.UserService;
import com.springboot.project.service.VerificationCodeEmailService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
    protected RolePermissionRelationService rolePermissionRelationService;

    @Autowired
    protected DistributedExecutionMainService distributedExecutionMainService;

    @Autowired
    protected RoleService roleService;

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
    protected DistributedExecutionMainFormatter distributedExecutionMainFormatter;

    @Autowired
    protected UserBlackOrganizeFormatter userBlackOrganizeFormatter;

    @Autowired
    protected RoleFormatter roleFormatter;

    @Autowired
    protected PermissionFormatter permissionFormatter;

    @Autowired
    protected DistributedExecutionDetailFormatter distributedExecutionDetailFormatter;

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

    protected <U> JPAJinqStream<U> streamAll(Class<U> entity) {
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