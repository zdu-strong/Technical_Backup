package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enums.SystemPermissionEnum;

@Service
public class RolePermissionRelationService extends BaseService {

    public void create(String roleId, SystemPermissionEnum permissionEnum) {
        var permissionName = permissionEnum.name();
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
