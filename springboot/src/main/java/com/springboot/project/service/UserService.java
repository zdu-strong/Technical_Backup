package com.springboot.project.service;

import java.util.ArrayList;
import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.UserEmailEntity;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.UserModel;
import cn.hutool.core.lang.UUID;

@Service
public class UserService extends BaseService {

    public UserModel createUserIfNotExist(String email) {
        if (this.UserEmailEntity().where(s -> s.getEmail().equals(email)).findFirst().isEmpty()) {
            this.createUser(email);
        }
        var userEntity = this.UserEmailEntity().where(s -> s.getEmail().equals(email)).select(UserEmailEntity::getUser)
                .getOnlyValue();
        return this.userFormatter.format(userEntity);
    }

    private void createUser(String email) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setCreateDate(new Date());
        userEntity.setUsername(email);
        userEntity.setUserEmailList(new ArrayList<>());
        this.entityManager.persist(userEntity);
        this.createUserEmail(email, userEntity.getId());
    }

    private void createUserEmail(String email, String userId) {
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).getOnlyValue();
        UserEmailEntity userEmailEntity = new UserEmailEntity();
        userEmailEntity.setId(UUID.randomUUID().toString());
        userEmailEntity.setCreateDate(new Date());
        userEmailEntity.setEmail(email);
        userEmailEntity.setUser(userEntity);
        this.entityManager.persist(userEmailEntity);
    }
}
