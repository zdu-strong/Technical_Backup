package com.springboot.project.service;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enums.SystemPermissionEnum;
import com.springboot.project.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserRoleRelationService extends BaseService {

    public void create(String userId, String userRoleId) {
        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(userRoleId))
                .getOnlyValue();

        var userRoleRelationEntity = new UserRoleRelationEntity();
        userRoleRelationEntity.setId(newId());
        userRoleRelationEntity.setCreateDate(new Date());
        userRoleRelationEntity.setUpdateDate(new Date());
        userRoleRelationEntity.setUser(userEntity);
        userRoleRelationEntity.setRole(roleEntity);
        this.persist(userRoleRelationEntity);
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
