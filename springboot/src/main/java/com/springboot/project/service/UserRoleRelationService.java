package com.springboot.project.service;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enums.SystemPermissionEnum;
import com.springboot.project.enums.SystemRoleEnum;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.RoleModel;
import com.springboot.project.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserRoleRelationService extends BaseService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePermissionRelationService rolePermissionRelationService;

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

     @Transactional(readOnly = true)
    public PaginationModel<RoleModel> searchUserRoleForSuperAdminByPagination(long pageNum, long pageSize) {
        var stream = this.streamAll(RoleEntity.class)
                .where(s -> !s.getIsOrganizeRole())
                .where(s -> s.getIsActive());
        return new PaginationModel<>(pageNum, pageSize, stream, this.roleFormatter::format);
    }

    public boolean refresh() {
        if (this.createDefaultUserRoleList()) {
            return true;
        }
        return false;
    }

    private boolean createDefaultUserRoleList() {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            if (systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            var roleName = systemRoleEnum.getValue();
            if (this.streamAll(RoleEntity.class)
                    .where(s -> !s.getIsOrganizeRole())
                    .where(s -> s.getName().equals(roleName))
                    .exists()) {
                if (this.refreshDefaultUserRoleList(systemRoleEnum)) {
                    return true;
                }
                continue;
            }
            this.roleService.create(roleName,
                    systemRoleEnum.getPermissionList(),
                    List.of());
            return true;
        }
        return false;
    }

    private boolean refreshDefaultUserRoleList(SystemRoleEnum systemRoleEnum) {
        var roleName = systemRoleEnum.getValue();
        var roleList = this.streamAll(RoleEntity.class)
                .where(s -> !s.getIsOrganizeRole())
                .where(s -> s.getName().equals(roleName))
                .toList();
        for (var roleEntity : roleList) {
            var roleId = roleEntity.getId();
            var permissionList = this.streamAll(RolePermissionRelationEntity.class)
                    .where(s -> s.getRole().getId().equals(roleId))
                    .toList();
            if (systemRoleEnum.getPermissionList().size() == permissionList.size()
                    && systemRoleEnum.getPermissionList().stream().allMatch(m -> permissionList.stream()
                            .anyMatch(n -> m.getValue().equals(n.getPermission().getName())))) {
                continue;
            }
            for (var permissionEntity : permissionList) {
                this.remove(permissionEntity);
            }
            for (var permissionEnum : systemRoleEnum.getPermissionList()) {
                this.rolePermissionRelationService.create(roleEntity.getId(), permissionEnum);
            }
            return true;
        }

        return false;
    }

}
