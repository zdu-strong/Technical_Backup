package com.springboot.project.service;

import java.util.Arrays;
import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.SystemDefaultRoleEntity;
import com.springboot.project.enumerate.SystemRoleEnum;

@Service
public class SystemDefaultRoleService extends BaseService {

    /**
     * 
     * @return hasNext boolean
     */
    public boolean refresh() {
        if (this.deleteSystemDefaultRoleList()) {
            return true;
        }
        if (this.createSystemDefaultRoleList()) {
            return true;
        }
        return false;
    }

    private boolean createSystemDefaultRoleList() {
        for (var systemRole : SystemRoleEnum.values()) {
            var role = systemRole.getRole();
            var exists = this.SystemDefaultRoleEntity()
                    .where(s -> s.getName().equals(role))
                    .exists();
            if (exists) {
                continue;
            }
            this.create(systemRole);
            return true;
        }
        return false;
    }

    private boolean deleteSystemDefaultRoleList() {
        var roleList = Arrays.stream(SystemRoleEnum.values()).map(s -> s.getRole()).toList();
        var systemDefaultRoleEntity = this.SystemDefaultRoleEntity()
                .where(s -> !roleList.contains(s.getName()))
                .findFirst()
                .orElse(null);
        if (systemDefaultRoleEntity != null) {
            var idOfSystemDefaultRoleEntity = systemDefaultRoleEntity.getId();
            var systemRoleRelationEntity = this.SystemDefaultRoleEntity()
                    .where(s -> s.getId().equals(idOfSystemDefaultRoleEntity))
                    .selectAllList(s -> s.getSystemRoleRelationList())
                    .findFirst()
                    .orElse(null);
            if (systemRoleRelationEntity != null) {
                this.remove(systemRoleRelationEntity);
                return true;
            }
            this.remove(systemDefaultRoleEntity);
            return true;
        }
        return false;
    }

    private void create(SystemRoleEnum systemRole) {
        var systemDefaultRoleEntity = new SystemDefaultRoleEntity();
        systemDefaultRoleEntity.setId(newId());
        systemDefaultRoleEntity.setName(systemRole.getRole());
        systemDefaultRoleEntity.setCreateDate(new Date());
        systemDefaultRoleEntity.setUpdateDate(new Date());
        this.persist(systemDefaultRoleEntity);
    }

}
