package com.springboot.project.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserRoleRelationEntity;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.UserModel;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserRoleRelationService extends BaseService {

    public void create(String userId, String userRoleId) {
        var userEntity = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        var userRoleEntity = this.UserRoleEntity()
                .where(s -> s.getId().equals(userRoleId))
                .getOnlyValue();

        var userRoleRelationEntity = new UserRoleRelationEntity();
        userRoleRelationEntity.setId(newId());
        userRoleRelationEntity.setCreateDate(new Date());
        userRoleRelationEntity.setUpdateDate(new Date());
        userRoleRelationEntity.setUser(userEntity);
        userRoleRelationEntity.setUserRole(userRoleEntity);
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
            if (this.permissionUtil.hasAnyRole(request, SystemRoleEnum.SUPER_ADMIN)) {
                break;
            }

            this.permissionUtil.checkAnyRole(request, organizeRoleRelation.getOrganize().getId(),
                    SystemRoleEnum.ORGANIZE_ADMIN);
        }

        if (!user.getUserRoleRelationList().isEmpty()) {
            this.permissionUtil.checkAnyRole(request, SystemRoleEnum.SUPER_ADMIN);
        }
    }

    private void checkRoleRelationForUpdate(UserModel user, HttpServletRequest request) {
        var userOne = this.userService.getUserWithMoreInformation(user.getId());

        for (var organizeRole : JinqStream.from(List.of(
                user.getOrganizeRoleRelationList().stream()
                        .filter(s -> !userOne.getOrganizeRoleRelationList().stream()
                                .anyMatch(t -> s.getId().equals(t.getId())))
                        .toList(),
                userOne.getOrganizeRoleRelationList().stream()
                        .filter(s -> user.getOrganizeRoleRelationList().stream()
                                .anyMatch(t -> s.getId().equals(t.getId())))
                        .toList()))
                .selectAllList(s -> s).toList()) {
            if (this.permissionUtil.hasAnyRole(request, SystemRoleEnum.SUPER_ADMIN)) {
                break;
            }

            var organizeRoleId = organizeRole.getId();
            var userRoleEntity = this.UserRoleEntity()
                    .where(s -> s.getId().equals(organizeRoleId))
                    .getOnlyValue();
            this.permissionUtil.checkAnyRole(request, userRoleEntity.getOrganize().getId(),
                    SystemRoleEnum.ORGANIZE_ADMIN);
        }

        if (JinqStream.from(List.of(
                user.getUserRoleRelationList().stream()
                        .filter(s -> !userOne.getUserRoleRelationList().stream()
                                .anyMatch(t -> s.getId().equals(t.getId())))
                        .toList(),
                userOne.getUserRoleRelationList().stream()
                        .filter(s -> user.getUserRoleRelationList().stream().anyMatch(t -> s.getId().equals(t.getId())))
                        .toList()))
                .selectAllList(s -> s).exists()) {
            this.permissionUtil.checkAnyRole(request, SystemRoleEnum.SUPER_ADMIN);
        }
    }

}
