package com.springboot.project.service;

import java.util.Arrays;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.PermissionEntity;
import com.springboot.project.enumerate.SystemPermissionEnum;
import com.springboot.project.model.PermissionModel;

@Service
public class PermissionService extends BaseService {

    /**
     * 
     * @return hasNext boolean
     */
    public boolean refresh() {
        if (this.deleteDefaultPermissionList()) {
            return true;
        }
        if (this.createDefaultPermissionList()) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public PermissionModel getById(String id) {
        var permissionEntity = this.streamAll(PermissionEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        return this.permissionFormatter.format(permissionEntity);
    }

    private boolean createDefaultPermissionList() {
        for (var permissionEnum : SystemPermissionEnum.values()) {
            var role = permissionEnum.name();
            var exists = this.streamAll(PermissionEntity.class)
                    .where(s -> s.getName().equals(role))
                    .exists();
            if (exists) {
                continue;
            }
            this.create(permissionEnum);
            return true;
        }
        return false;
    }

    private boolean deleteDefaultPermissionList() {
        var roleList = Arrays.stream(SystemPermissionEnum.values()).map(s -> s.name()).toList();
        var systemRoleEntity = this.streamAll(PermissionEntity.class)
                .where(s -> !roleList.contains(s.getName()))
                .findFirst()
                .orElse(null);
        if (systemRoleEntity != null) {
            var idOfSystemRoleEntity = systemRoleEntity.getId();
            var rolePermissionRelationEntity = this.streamAll(PermissionEntity.class)
                    .where(s -> s.getId().equals(idOfSystemRoleEntity))
                    .selectAllList(s -> s.getRolePermissionRelationList())
                    .findFirst()
                    .orElse(null);
            if (rolePermissionRelationEntity != null) {
                this.remove(rolePermissionRelationEntity);
                return true;
            }
            this.remove(systemRoleEntity);
            return true;
        }
        return false;
    }

    private void create(SystemPermissionEnum permissionEnum) {
        var permissionEntity = new PermissionEntity();
        permissionEntity.setId(newId());
        permissionEntity.setName(permissionEnum.name());
        permissionEntity.setCreateDate(new Date());
        permissionEntity.setUpdateDate(new Date());
        this.persist(permissionEntity);
    }

}
