package com.springboot.project.service;

import java.util.Arrays;
import java.util.Date;

import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.enumerate.SystemRoleEnum;
import com.springboot.project.entity.SystemDefaultRoleEntity;

@Service
public class SystemDefaultRoleService extends BaseService {

    /**
     * 
     * @return hasNext boolean
     */
    public boolean refresh() {
        for (var systemRole : SystemRoleEnum.values()) {
            var role = systemRole.getValue();
            var exists = this.SystemDefaultRoleEntity()
                    .where(s -> s.getName().equals(role))
                    .exists();
            if (exists) {
                continue;
            }
            this.create(systemRole);
        }

        var roles = Arrays.stream(SystemRoleEnum.values()).map(s -> s.getValue()).toList();
        var systemRoleRelationEntity = this.SystemDefaultRoleEntity()
                .where(s -> !roles.contains(s.getName()))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .findFirst()
                .orElse(null);
        if (systemRoleRelationEntity != null) {
            this.remove(systemRoleRelationEntity);
            return true;
        }

        var systemRoleEntity = this.SystemDefaultRoleEntity()
                .where(s -> !roles.contains(s.getName()))
                .findFirst()
                .orElse(null);
        if (systemRoleEntity != null) {
            this.remove(systemRoleEntity);
            return true;
        }

        return false;
    }

    private void create(SystemRoleEnum systemRole) {
        var systemDefaultRoleEntity = new SystemDefaultRoleEntity();
        systemDefaultRoleEntity.setId(newId());
        systemDefaultRoleEntity.setName(systemRole.getValue());
        systemDefaultRoleEntity.setCreateDate(new Date());
        systemDefaultRoleEntity.setUpdateDate(new Date());
        this.persist(systemDefaultRoleEntity);
    }

}
