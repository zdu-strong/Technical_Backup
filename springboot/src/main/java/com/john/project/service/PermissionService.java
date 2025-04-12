package com.john.project.service;

import java.util.Arrays;
import java.util.Date;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.PermissionEntity;
import com.john.project.entity.RolePermissionRelationEntity;
import com.john.project.enums.SystemPermissionEnum;

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

    private boolean createDefaultPermissionList() {
        for (var permissionEnum : SystemPermissionEnum.values()) {
            var permissionName = permissionEnum.getValue();
            var exists = this.streamAll(PermissionEntity.class)
                    .where(s -> s.getName().equals(permissionName))
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
        var permissionNameList = Arrays.stream(SystemPermissionEnum.values()).map(s -> s.getValue()).toList();
        var permissionEntity = this.streamAll(PermissionEntity.class)
                .where(s -> !permissionNameList.contains(s.getName()))
                .findFirst()
                .orElse(null);
        if (permissionEntity != null) {
            var idOfPermission = permissionEntity.getId();
            var rolePermissionRelationEntity = this.streamAll(RolePermissionRelationEntity.class)
                    .where(s -> s.getPermission().getId().equals(idOfPermission))
                    .findFirst()
                    .orElse(null);
            if (rolePermissionRelationEntity != null) {
                this.remove(rolePermissionRelationEntity);
                return true;
            }
            this.remove(permissionEntity);
            return true;
        }
        return false;
    }

    private void create(SystemPermissionEnum permissionEnum) {
        var permissionEntity = new PermissionEntity();
        permissionEntity.setId(newId());
        permissionEntity.setName(permissionEnum.getValue());
        permissionEntity.setCreateDate(new Date());
        permissionEntity.setUpdateDate(new Date());
        this.persist(permissionEntity);
    }

}
