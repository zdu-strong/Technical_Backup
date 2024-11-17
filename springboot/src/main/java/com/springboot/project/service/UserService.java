package com.springboot.project.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.UserModel;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.StrFormatter;

@Service
public class UserService extends BaseService {

    public UserModel create(UserModel userModel) {
        var id = newId();
        var password = this.encryptDecryptService.encryptByAES(id,
                this.encryptDecryptService.generateSecretKeyOfAES(userModel.getPassword()));
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
            this.userRoleRelationService.create(userEntity.getId(), userRoleRelation.getUserRole().getId(), null);
        }

        for (var organizeRelation : userModel.getOrganizeRoleRelationList()) {
            this.userRoleRelationService.create(userEntity.getId(), organizeRelation.getUserRole().getId(), organizeRelation.getOrganize().getId());
        }

        return this.userFormatter.formatWithMoreInformation(userEntity);
    }

    public void update(UserModel userModel) {
        var userId = userModel.getId();
        var userEntity = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();

        for (var userRoleRelationEntity : userEntity.getUserRoleRelationList()) {
            this.remove(userRoleRelationEntity);
        }

        for (var userRoleRelation : userModel.getUserRoleRelationList()) {
            this.userRoleRelationService.create(userId, userRoleRelation.getUserRole().getId(), null);
        }

        for (var organizeRelation : userModel.getOrganizeRoleRelationList()) {
            this.userRoleRelationService.create(userId, organizeRelation.getUserRole().getId(), organizeRelation.getOrganize().getId());
        }

        this.merge(userEntity);
    }

    @Transactional(readOnly = true)
    public UserModel getUserWithMoreInformation(String id) {
        var user = this.UserEntity().where(s -> s.getId().equals(id)).where(s -> s.getIsActive())
                .getOnlyValue();
        return this.userFormatter.formatWithMoreInformation(user);
    }

    @Transactional(readOnly = true)
    public UserModel getUser(String id) {
        var user = this.UserEntity().where(s -> s.getId().equals(id)).where(s -> s.getIsActive())
                .getOnlyValue();
        return this.userFormatter.format(user);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public PaginationModel<UserModel> searchForSuperAdminByPagination(long pageNum, long pageSize) {
        var stream = this.UserEntity()
                .where(s -> s.getIsActive())
                .sortedDescendingBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.userFormatter.format(s));
    }

    @Transactional(readOnly = true)
    public void checkValidEmailForSignUp(UserModel userModel) {
        for (var userEmail : userModel.getUserEmailList()) {
            if (StringUtils.isBlank(userEmail.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
            }

            if (!Validator.isEmail(userEmail.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
            }

            if (StringUtils.isBlank(userEmail.getVerificationCodeEmail().getVerificationCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        StrFormatter.format("The verification code of email {} cannot be empty", userEmail.getEmail()));
            }

            userEmail.getVerificationCodeEmail().setEmail(userEmail.getEmail());

            this.verificationCodeEmailService
                    .checkVerificationCodeEmailHasBeenUsed(userEmail.getVerificationCodeEmail());

            this.verificationCodeEmailService
                    .checkVerificationCodeEmailIsPassed(userEmail.getVerificationCodeEmail());

            this.userEmailService.checkIsNotUsedOfEmail(userEmail.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public void checkCannotEmptyOfUsername(UserModel userModel) {
        if (StringUtils.isBlank(userModel.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in nickname");
        }

        if (userModel.getUsername().trim().length() != userModel.getUsername().length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }
    }

    @Transactional(readOnly = true)
    public void checkExistUserById(String id) {
        if (!hasExistsUserId(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkExistAccount(String account) {
        if (!hasExistsUserId(account) && !hasExistEmail(account)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect username or password");
        }
    }

    @Transactional(readOnly = true)
    public void checkCannotEmptyOfPassword(UserModel userModel) {
        if (StringUtils.isBlank(userModel.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in password");
        }

        if (userModel.getPassword().trim().length() != userModel.getPassword().length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot start or end with a space");
        }
    }

    private boolean hasExistsUserId(String userId) {
        var exists = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .where(s -> s.getIsActive())
                .exists();
        return exists;
    }

    private boolean hasExistEmail(String email) {
        var exists = this.UserEmailEntity()
                .where(s -> s.getEmail().equals(email))
                .where(s -> s.getIsActive())
                .where(s -> s.getUser().getIsActive())
                .exists();
        return exists;
    }

}
