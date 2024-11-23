package com.springboot.project.service;

import java.util.Arrays;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.SystemRoleEntity;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.SystemRoleModel;

@Service
public class SystemRoleService extends BaseService {

    /**
     * 
     * @return hasNext boolean
     */
    public boolean refresh() {
        if (this.deleteSystemRoleList()) {
            return true;
        }
        if (this.createSystemRoleList()) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public SystemRoleModel getById(String id) {
        var systemRoleEntity = this.streamAll(SystemRoleEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        return this.systemRoleFormatter.format(systemRoleEntity);
    }

    private boolean createSystemRoleList() {
        for (var systemRole : SystemRoleEnum.values()) {
            var role = systemRole.name();
            var exists = this.streamAll(SystemRoleEntity.class)
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

    private boolean deleteSystemRoleList() {
        var roleList = Arrays.stream(SystemRoleEnum.values()).map(s -> s.name()).toList();
        var systemRoleEntity = this.streamAll(SystemRoleEntity.class)
                .where(s -> !roleList.contains(s.getName()))
                .findFirst()
                .orElse(null);
        if (systemRoleEntity != null) {
            var idOfSystemRoleEntity = systemRoleEntity.getId();
            var systemRoleRelationEntity = this.streamAll(SystemRoleEntity.class)
                    .where(s -> s.getId().equals(idOfSystemRoleEntity))
                    .selectAllList(s -> s.getSystemRoleRelationList())
                    .findFirst()
                    .orElse(null);
            if (systemRoleRelationEntity != null) {
                this.remove(systemRoleRelationEntity);
                return true;
            }
            this.remove(systemRoleEntity);
            return true;
        }
        return false;
    }

    private void create(SystemRoleEnum systemRole) {
        var systemRoleEntity = new SystemRoleEntity();
        systemRoleEntity.setId(newId());
        systemRoleEntity.setName(systemRole.name());
        systemRoleEntity.setCreateDate(new Date());
        systemRoleEntity.setUpdateDate(new Date());
        this.persist(systemRoleEntity);
    }

}
