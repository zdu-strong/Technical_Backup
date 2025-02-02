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

    // @Autowired
    // private UserService userService;

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

        // for (var organizeRoleRelation : user.getOrganizeRoleRelationList()) {
        //     if (this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN)) {
        //         break;
        //     }

        //     this.permissionUtil.checkAnyPermission(request, organizeRoleRelation.getOrganize().getId(),
        //             SystemPermissionEnum.ORGANIZE_MANAGE);
        // }

        if (!user.getRoleList().isEmpty()) {
            this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);
        }
    }

    private void checkRoleRelationForUpdate(UserModel user, HttpServletRequest request) {
        // var userOne = this.userService.getUserWithMoreInformation(user.getId());

        // for (var organizeRole : JinqStream.from(List.of(
        //         user.getOrganizeRoleRelationList().stream()
        //                 .filter(s -> !userOne.getOrganizeRoleRelationList()
        //                         .stream()
        //                         .anyMatch(t -> s.getId().equals(t.getId())))
        //                 .toList(),
        //         userOne.getOrganizeRoleRelationList()
        //                 .stream()
        //                 .filter(s -> user.getOrganizeRoleRelationList()
        //                         .stream()
        //                         .anyMatch(t -> s.getId().equals(t.getId())))
        //                 .toList()))
        //         .selectAllList(s -> s).toList()) {
        //     if (this.permissionUtil.hasAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN)) {
        //         break;
        //     }

        //     var organizeRoleId = organizeRole.getId();
        //     var roleEntity = this.streamAll(RoleEntity.class)
        //             .where(s -> s.getId().equals(organizeRoleId))
        //             .getOnlyValue();
        //     this.permissionUtil.checkAnyPermission(request, roleEntity.getOrganize().getId(),
        //             SystemPermissionEnum.ORGANIZE_MANAGE);
        // }

        // if (JinqStream.from(List.of(
        //         user.getUserRoleRelationList()
        //                 .stream()
        //                 .filter(s -> !userOne.getUserRoleRelationList().stream()
        //                         .anyMatch(t -> s.getId().equals(t.getId())))
        //                 .toList(),
        //         userOne.getUserRoleRelationList()
        //                 .stream()
        //                 .filter(s -> user.getUserRoleRelationList().stream().anyMatch(t -> s.getId().equals(t.getId())))
        //                 .toList()))
        //         .selectAllList(s -> s).exists()) {
        //     this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);
        // }
    }

}
