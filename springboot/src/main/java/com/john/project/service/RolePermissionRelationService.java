package com.john.project.service;

import java.util.Date;

import com.john.project.entity.PermissionEntity;
import com.john.project.entity.RoleEntity;
import com.john.project.entity.RolePermissionRelationEntity;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.*;
import com.john.project.enums.SystemPermissionEnum;

@Service
public class RolePermissionRelationService extends BaseService {

    public void create(String roleId, SystemPermissionEnum permissionEnum) {
        var permissionName = permissionEnum.getValue();
        var permissionEntity = this.streamAll(PermissionEntity.class)
                .where(s -> s.getName().equals(permissionName))
                .getOnlyValue();
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(roleId))
                .getOnlyValue();

        var rolePermissionRelationEntity = new RolePermissionRelationEntity();
        rolePermissionRelationEntity.setId(newId());
        rolePermissionRelationEntity.setCreateDate(new Date());
        rolePermissionRelationEntity.setUpdateDate(new Date());
        rolePermissionRelationEntity.setPermission(permissionEntity);
        rolePermissionRelationEntity.setRole(roleEntity);
        this.persist(rolePermissionRelationEntity);
    }

}
