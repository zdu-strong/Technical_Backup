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
public class RoleOrganizeRelationService extends BaseService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePermissionRelationService rolePermissionRelationService;

    public void create(String roleId, String organizeId) {
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(roleId))
                .getOnlyValue();

        var roleOrganizeRelationEntity = new RoleOrganizeRelationEntity();
        roleOrganizeRelationEntity.setId(newId());
        roleOrganizeRelationEntity.setCreateDate(new Date());
        roleOrganizeRelationEntity.setUpdateDate(new Date());
        roleOrganizeRelationEntity.setRole(roleEntity);
        roleOrganizeRelationEntity.setOrganize(organizeEntity);
        this.persist(roleOrganizeRelationEntity);
    }

    public boolean refresh(String organizeId) {
        return this.createDefaultOrganizeRoleList(organizeId);
    }

    @Transactional(readOnly = true)
    public PaginationModel<RoleModel> searchOrganizeRoleForSuperAdminByPagination(long pageNum, long pageSize,
            String organizeId, boolean isIncludeDescendant) {
        var roleOrganizeRelationStream = this.streamAll(RoleOrganizeRelationEntity.class)
                .joinList(s -> s.getOrganize().getAncestorList())
                .where(s -> s.getTwo().getAncestor().getId().equals(organizeId));
        if (!isIncludeDescendant) {
            roleOrganizeRelationStream = roleOrganizeRelationStream
                    .where(s -> s.getTwo().getDescendant().getId().equals(organizeId));
        }
        var stream = roleOrganizeRelationStream.select(s -> s.getOne().getRole());
        return new PaginationModel<>(pageNum, pageSize, stream, this.roleFormatter::format);
    }

    private boolean createDefaultOrganizeRoleList(String organizeId) {
        for (var systemRoleEnum : SystemRoleEnum.values()) {
            var roleName = systemRoleEnum.getValue();
            if (!systemRoleEnum.getIsOrganizeRole()) {
                continue;
            }
            if (this.streamAll(RoleOrganizeRelationEntity.class)
                    .where(s -> s.getRole().getIsOrganizeRole())
                    .where(s -> s.getRole().getName().equals(roleName))
                    .where(s -> s.getOrganize().getId().equals(organizeId))
                    .exists()) {
                if (this.refreshDefaultOrganizeRoleList(organizeId, systemRoleEnum)) {
                    return true;
                }
                continue;
            }
            this.roleService.create(roleName,
                    systemRoleEnum.getPermissionList(),
                    List.of(organizeId));
            return true;
        }
        return false;
    }

    private boolean refreshDefaultOrganizeRoleList(String organizeId, SystemRoleEnum systemRoleEnum) {
        var roleName = systemRoleEnum.getValue();
        var roleList = this.streamAll(RoleOrganizeRelationEntity.class)
                .where(s -> s.getRole().getIsOrganizeRole())
                .where(s -> s.getRole().getName().equals(roleName))
                .where(s -> s.getOrganize().getId().equals(organizeId))
                .select(s -> s.getRole())
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
