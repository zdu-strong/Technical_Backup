package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.UserModel;

@Service
public class UserService extends BaseService {

    public UserModel create(UserModel userModel) {
        var id = newId();
        var password = this.encryptDecryptService.encryptByAES(id, this.encryptDecryptService.generateSecretKeyOfAES(userModel.getPassword()));
        var userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setUsername(userModel.getUsername());
        userEntity.setPassword(password);
        userEntity.setIsActive(true);
        userEntity.setCreateDate(new Date());
        userEntity.setUpdateDate(new Date());
        this.persist(userEntity);

        for (var userEmail : userModel.getUserEmailList()) {
            this.userEmailService.createUserEmail(userEmail.getEmail(), userEntity.getId());
        }

        for (var userRoleRelation : userModel.getUserRoleRelationList()) {
            this.userRoleRelationService.create(userEntity.getId(), userRoleRelation.getSystemRole().getId());
        }

        for (var organizeRelation : userModel.getOrganizeRoleRelationList()) {
            this.userRoleRelationService.create(userEntity.getId(), organizeRelation.getSystemRole().getId());
        }

        return this.userFormatter.formatWithMoreInformation(userEntity);
    }

    public void update(UserModel userModel) {
        var userId = userModel.getId();
        var userEntity = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();

        for (var userSystemRoleRelationEntity : userEntity.getUserSystemRoleRelationList()) {
            this.remove(userSystemRoleRelationEntity);
        }

        for (var userRoleRelation : userModel.getUserRoleRelationList()) {
            this.userRoleRelationService.create(userId, userRoleRelation.getSystemRole().getId());
        }

        for (var organizeRelation : userModel.getOrganizeRoleRelationList()) {
            this.userRoleRelationService.create(userId, organizeRelation.getSystemRole().getId());
        }

        this.merge(userEntity);
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
