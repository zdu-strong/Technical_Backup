package com.springboot.project.service;

import java.util.Arrays;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.project.common.mysql.MysqlFunction;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.entity.EncryptDecryptEntity;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.entity.UserEmailEntity;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.format.LongTermTaskFormatter;
import com.springboot.project.format.OrganizeFormatter;
import com.springboot.project.format.StorageSpaceFormatter;
import com.springboot.project.format.UserEmailFormatter;
import com.springboot.project.format.UserFormatter;
import com.springboot.project.format.UserMessageFormatter;

@Service
@Transactional(rollbackFor = Throwable.class)
public abstract class BaseService {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected Storage storage;

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

    private <U> JPAJinqStream<U> streamAll(Class<U> entity) {
        JinqJPAStreamProvider jinqJPAStreamProvider = new JinqJPAStreamProvider(entityManager.getMetamodel());
        for (var method : Arrays.asList(MysqlFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("ifnull")).collect(Collectors.toList())) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method, "IFNULL");
        }
        for (var method : Arrays.asList(MysqlFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("foundTotalRowsForGroupBy")).collect(Collectors.toList())) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method, "FOUND_ROWS");
        }
        for (var method : Arrays.asList(MysqlFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("isChildOrganize")).collect(Collectors.toList())) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method, "IS_CHILD_ORGANIZE");
        }
        for (var method : Arrays.asList(MysqlFunction.class.getMethods()).stream()
                .filter(s -> s.getName().equals("isSortAtBefore")).collect(Collectors.toList())) {
            jinqJPAStreamProvider.registerCustomSqlFunction(method, "IS_SORT_AT_BEFORE");
        }

        return jinqJPAStreamProvider.streamAll(entityManager, entity);
    }

}