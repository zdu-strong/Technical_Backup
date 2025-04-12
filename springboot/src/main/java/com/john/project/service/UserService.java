package com.john.project.service;

import java.util.Date;
import java.util.List;

import com.john.project.entity.UserEmailEntity;
import com.john.project.entity.UserEntity;
import com.john.project.model.SuperAdminUserQueryPaginationModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.common.baseService.BaseService;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.PaginationModel;
import com.john.project.model.UserModel;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.StrFormatter;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService extends BaseService {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    @Autowired
    private UserRoleRelationService userRoleRelationService;

    @Autowired
    private VerificationCodeEmailService verificationCodeEmailService;

    @Autowired
    private UserEmailService userEmailService;

    public UserModel create(UserModel userModel) {
        var id = newId();
        var password = this.encryptDecryptService.encryptByAES(id,
                this.encryptDecryptService.generateSecretKeyOfAES(id + userModel.getPassword()));
        var userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setUsername(userModel.getUsername());
        userEntity.setPassword(password);
        userEntity.setIsDeleted(false);
        userEntity.setCreateDate(new Date());
        userEntity.setUpdateDate(new Date());
        this.persist(userEntity);

        for (var userEmailModel : userModel.getUserEmailList()) {
            this.userEmailService.createUserEmail(userEmailModel.getEmail(), userEntity.getId());
        }

        for (var roleModel : userModel.getRoleList()) {
            this.userRoleRelationService.create(userEntity.getId(), roleModel.getId());
        }

        return this.userFormatter.formatWithMoreInformation(userEntity);
    }

    public void update(UserModel userModel) {
        var userId = userModel.getId();
        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        this.merge(userEntity);

        var userRoleRelationList = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .selectAllList(s -> s.getUserRoleRelationList())
                .toList();
        for (var userRoleRelationEntity : userRoleRelationList) {
            if (JinqStream.from(userModel.getRoleList())
                    .select(s -> s.getId())
                    .toList()
                    .contains(userRoleRelationEntity.getRole().getId())) {
                continue;
            }
            this.remove(userRoleRelationEntity);
        }

        for (var roleModel : userModel.getRoleList()) {
            if (JinqStream.from(userRoleRelationList)
                    .select(s -> s.getRole().getId())
                    .toList()
                    .contains(roleModel.getId())) {
                continue;
            }
            this.userRoleRelationService.create(userId, roleModel.getId());
        }
    }

    @Transactional(readOnly = true)
    public UserModel getUserWithMoreInformation(String id) {
        var user = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .getOnlyValue();
        return this.userFormatter.formatWithMoreInformation(user);
    }

    @Transactional(readOnly = true)
    public UserModel getUser(String id) {
        var user = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .getOnlyValue();
        return this.userFormatter.format(user);
    }

    @Transactional(readOnly = true)
    public String getUserId(String account) {
        {
            var userId = account;
            var userEntity = this.streamAll(UserEntity.class)
                    .where(s -> s.getId().equals(userId))
                    .where(s -> !s.getIsDeleted())
                    .findOne()
                    .orElse(null);
            if (userEntity != null) {
                return userEntity.getId();
            }
        }
        {
            var email = account;
            var userEntity = this.streamAll(UserEmailEntity.class)
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> !s.getIsDeleted())
                    .where(s -> !s.getUser().getIsDeleted())
                    .select(s -> s.getUser())
                    .getOnlyValue();
            return userEntity.getId();
        }
    }

    @Transactional(readOnly = true)
    public PaginationModel<UserModel> searchForSuperAdminByPagination(SuperAdminUserQueryPaginationModel superAdminUserQueryPaginationModel) {
        var stream = this.streamAll(UserEntity.class)
                .where(s -> !s.getIsDeleted())
                .sortedDescendingBy(s -> s.getCreateDate());
        return new PaginationModel<>(superAdminUserQueryPaginationModel.getPageNum(), superAdminUserQueryPaginationModel.getPageSize(), stream, (s) -> this.userFormatter.format(s));
    }

    @Transactional(readOnly = true)
    public void checkValidEmailForSignUp(UserModel userModel) {
        for (var userEmail : userModel.getUserEmailList()) {
            this.validationFieldUtil.checkNotBlankOfEmail(userEmail.getEmail());
            this.validationFieldUtil.checkCorrectFormatOfEmail(userEmail.getEmail());

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

    private boolean hasExistsUserId(String userId) {
        var exists = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .where(s -> !s.getIsDeleted())
                .exists();
        return exists;
    }

    private boolean hasExistEmail(String email) {
        var exists = this.streamAll(UserEmailEntity.class)
                .where(s -> s.getEmail().equals(email))
                .where(s -> !s.getIsDeleted())
                .where(s -> !s.getUser().getIsDeleted())
                .exists();
        return exists;
    }

    @Transactional(readOnly = true)
    public void checkRoleRelation(UserModel user, HttpServletRequest request) {
        if (StringUtils.isBlank(user.getId())) {
            checkRoleRelationForCreate(user, request);
        } else {
            checkRoleRelationForUpdate(user, request);
        }
    }

    @Transactional(readOnly = true)
    public void checkUserRoleRelationListMustBeEmpty(UserModel user) {
        if (CollectionUtils.isEmpty(user.getRoleList())) {
            user.setRoleList(List.of());
        }
        if (!CollectionUtils.isEmpty(user.getRoleList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleList must be empty");
        }
    }

    private void checkRoleRelationForCreate(UserModel user, HttpServletRequest request) {
        if (!user.getRoleList().isEmpty()) {
            this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);
        }
    }

    private void checkRoleRelationForUpdate(UserModel user, HttpServletRequest request) {
    }
}
