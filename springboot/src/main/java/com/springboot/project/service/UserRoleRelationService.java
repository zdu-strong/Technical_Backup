package com.springboot.project.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enumerate.SystemPermissionEnum;
import com.springboot.project.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserRoleRelationService extends BaseService {

    public void create(String userId, String userRoleId, String organizeId) {
        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        var userRoleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(userRoleId))
                .getOnlyValue();
        var organizeEntity = Optional.ofNullable(organizeId)
                .map(s -> this.streamAll(OrganizeEntity.class)
                        .where(m -> m.getId().equals(organizeId))
                        .getOnlyValue())
                .orElse(null);

        var userRoleRelationEntity = new UserRoleRelationEntity();
        userRoleRelationEntity.setId(newId());
        userRoleRelationEntity.setCreateDate(new Date());
        userRoleRelationEntity.setUpdateDate(new Date());
        userRoleRelationEntity.setUser(userEntity);
        userRoleRelationEntity.setRole(userRoleEntity);
        userRoleRelationEntity.setOrganize(organizeEntity);
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
        if (CollectionUtils.isEmpty(user.getUserRoleRelationList())) {
            user.setUserRoleRelationList(List.of());
        }
        if (!CollectionUtils.isEmpty(user.getUserRoleRelationList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserRoleRelationList must be empty");
        }
    }

    @Transactional(readOnly = true)
    public void checkOrganizeRoleRelationListMustBeEmpty(UserModel user) {
        if (CollectionUtils.isEmpty(user.getOrganizeRoleRelationList())) {
            user.setOrganizeRoleRelationList(List.of());
        }
        if (!CollectionUtils.isEmpty(user.getOrganizeRoleRelationList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrganizeRoleRelationList must be empty");
        }
    }

    private void checkRoleRelationForCreate(UserModel user, HttpServletRequest request) {

        for (var organizeRoleRelation : user.getOrganizeRoleRelationList()) {
            if (this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN_PERMISSION)) {
                break;
            }

            this.permissionUtil.checkAnyPermission(request, organizeRoleRelation.getOrganize().getId(),
                    SystemPermissionEnum.ORGANIZE_MANAGE_PERMISSION);
        }

        if (!user.getUserRoleRelationList().isEmpty()) {
            this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN_PERMISSION);
        }
    }

    private void checkRoleRelationForUpdate(UserModel user, HttpServletRequest request) {
        var userOne = this.userService.getUserWithMoreInformation(user.getId());

        for (var organizeRole : JinqStream.from(List.of(
                user.getOrganizeRoleRelationList().stream()
                        .filter(s -> !userOne.getOrganizeRoleRelationList()
                                .stream()
                                .anyMatch(t -> s.getId().equals(t.getId())))
                        .toList(),
                userOne.getOrganizeRoleRelationList()
                        .stream()
                        .filter(s -> user.getOrganizeRoleRelationList()
                                .stream()
                                .anyMatch(t -> s.getId().equals(t.getId())))
                        .toList()))
                .selectAllList(s -> s).toList()) {
            if (this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN_PERMISSION)) {
                break;
            }

            var organizeRoleId = organizeRole.getId();
            var userRoleEntity = this.streamAll(RoleEntity.class)
                    .where(s -> s.getId().equals(organizeRoleId))
                    .getOnlyValue();
            this.permissionUtil.checkAnyPermission(request, userRoleEntity.getOrganize().getId(),
                    SystemPermissionEnum.ORGANIZE_MANAGE_PERMISSION);
        }

        if (JinqStream.from(List.of(
                user.getUserRoleRelationList()
                        .stream()
                        .filter(s -> !userOne.getUserRoleRelationList().stream()
                                .anyMatch(t -> s.getId().equals(t.getId())))
                        .toList(),
                userOne.getUserRoleRelationList()
                        .stream()
                        .filter(s -> user.getUserRoleRelationList().stream().anyMatch(t -> s.getId().equals(t.getId())))
                        .toList()))
                .selectAllList(s -> s).exists()) {
            this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN_PERMISSION);
        }
    }

}
