package com.springboot.project.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enums.SystemRoleEnum;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.RoleModel;

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

    public boolean refresh() {
        if (this.createDefaultUserRoleList()) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public PaginationModel<RoleModel> searchUserRoleForSuperAdminByPagination(long pageNum, long pageSize) {
        var stream = this.streamAll(RoleEntity.class)
                .where(s -> !s.getIsOrganizeRole())
                .where(s -> s.getIsActive());
        return new PaginationModel<>(pageNum, pageSize, stream, this.roleFormatter::format);
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
