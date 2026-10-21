package com.john.project.common.baseService;

import com.google.cloud.spanner.AbortedDueToConcurrentModificationException;
import com.john.project.common.FieldValidationUtil.ValidationFieldUtil;
import com.john.project.common.uuid.UUIDUtil;
import com.john.project.format.*;
import io.grpc.StatusRuntimeException;
import org.hibernate.exception.GenericJDBCException;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import com.google.cloud.spanner.AbortedException;
import com.john.project.common.TimeZoneUtil.TimeZoneUtil;
import com.john.project.common.database.JPQLFunction;
import com.john.project.common.permission.PermissionUtil;
import com.john.project.properties.DatabaseJdbcProperties;
import com.john.project.properties.DevelopmentMockModeProperties;
import com.john.project.common.storage.Storage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional(rollbackFor = Throwable.class)
@Retryable(maxRetries = 10, includes = {
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
    @Lazy
    protected Storage storage;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    @Lazy
    protected PermissionUtil permissionUtil;

    @Autowired
    @Lazy
    protected TimeZoneUtil timeZoneUtil;

    @Autowired
    @Lazy
    protected UUIDUtil uuidUtil;

    @Autowired
    @Lazy
    protected ValidationFieldUtil validationFieldUtil;

    @Autowired
    private DatabaseJdbcProperties databaseJdbcProperties;

    @Autowired
    protected DevelopmentMockModeProperties developmentMockModeProperties;

    @Autowired
    @Lazy
    protected TokenFormatter tokenFormatter;

    @Autowired
    @Lazy
    protected StorageSpaceFormatter storageSpaceFormatter;

    @Autowired
    @Lazy
    protected UserEmailFormatter userEmailFormatter;

    @Autowired
    @Lazy
    protected UserFormatter userFormatter;

    @Autowired
    @Lazy
    protected LongTermTaskFormatter longTermTaskFormatter;

    @Autowired
    @Lazy
    protected OrganizeFormatter organizeFormatter;

    @Autowired
    @Lazy
    protected UserMessageFormatter userMessageFormatter;

    @Autowired
    @Lazy
    protected FriendshipFormatter friendshipFormatter;

    @Autowired
    @Lazy
    protected LoggerFormatter loggerFormatter;

    @Autowired
    @Lazy
    protected NonceFormatter nonceFormatter;

    @Autowired
    @Lazy
    protected VerificationCodeEmailFormatter verificationCodeEmailFormatter;

    @Autowired
    @Lazy
    protected DistributedExecutionMainFormatter distributedExecutionMainFormatter;

    @Autowired
    @Lazy
    protected RoleFormatter roleFormatter;

    @Autowired
    @Lazy
    protected DistributedExecutionDetailFormatter distributedExecutionDetailFormatter;

    @Autowired
    @Lazy
    protected PermissionRelationFormatter permissionRelationFormatter;

    @Autowired
    @Lazy
    protected IdentityCardFormatter identityCardFormatter;

    @Autowired
    @Lazy
    protected EncryptDecryptFormatter encryptDecryptFormatter;

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
        if (this.databaseJdbcProperties.getIsNewSqlDatabase()) {
            return this.uuidUtil.v4();
        } else {
            return this.uuidUtil.v7();
        }
    }

}