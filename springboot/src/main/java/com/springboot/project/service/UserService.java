package com.springboot.project.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.UserModel;

@Service
public class UserService extends BaseService {

    public UserModel signUp(UserModel userModel) {
        var userEntity = new UserEntity();
        userEntity.setId(newId());
        userEntity.setUsername(userModel.getUsername());
        userEntity.setPrivateKeyOfRSA(userModel.getPrivateKeyOfRSA());
        userEntity.setPublicKeyOfRSA(userModel.getPublicKeyOfRSA());
        userEntity.setPassword(userModel.getPassword());
        userEntity.setIsActive(true);
        userEntity.setCreateDate(new Date());
        userEntity.setUpdateDate(new Date());
        this.persist(userEntity);

        for (var userEmail : userModel.getUserEmailList()) {
            this.userEmailService.createUserEmail(userEmail.getEmail(), userEntity.getId());
        }

        return this.userFormatter.format(userEntity);
    }

    public UserModel getUserWithMoreInformation(String id) {
        var user = this.UserEntity().where(s -> s.getId().equals(id)).where(s -> s.getIsActive())
                .getOnlyValue();
        return this.userFormatter.formatWithMoreInformation(user);
    }

    public UserModel getUser(String id) {
        var user = this.UserEntity().where(s -> s.getId().equals(id)).where(s -> s.getIsActive())
                .getOnlyValue();
        return this.userFormatter.format(user);
    }

    public String getUserId(String account) {
        {
            var userId = account;
            var userEntity = this.UserEntity()
                    .where(s -> s.getId().equals(userId))
                    .where(s -> s.getIsActive())
                    .findOne()
                    .orElse(null);
            if (userEntity != null) {
                return userEntity.getId();
            }
        }
        {
            var email = account;
            var userEntity = this.UserEmailEntity().where(s -> s.getEmail().equals(email))
                    .where(s -> s.getIsActive())
                    .where(s -> s.getUser().getIsActive())
                    .select(s -> s.getUser())
                    .getOnlyValue();
            return userEntity.getId();
        }
    }

}
