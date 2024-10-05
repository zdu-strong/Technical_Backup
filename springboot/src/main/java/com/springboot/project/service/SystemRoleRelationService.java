package com.springboot.project.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.SystemRoleRelationEntity;

@Service
public class SystemRoleRelationService extends BaseService {

    public void create(String systemRoleId, String systemDefaultRoleId) {
        var systemDefaultRoleEntity = this.SystemDefaultRoleEntity().where(s -> s.getId().equals(systemDefaultRoleId))
                .getOnlyValue();
        var systemRoleEntity = this.SystemRoleEntity().where(s -> s.getId().equals(systemRoleId)).getOnlyValue();

        var systemRoleRelationEntity = new SystemRoleRelationEntity();
        systemRoleRelationEntity.setId(newId());
        systemRoleRelationEntity.setCreateDate(new Date());
        systemRoleRelationEntity.setUpdateDate(new Date());
        systemRoleRelationEntity.setSystemDefaultRole(systemDefaultRoleEntity);
        systemRoleRelationEntity.setSystemRole(systemRoleEntity);
        this.persist(systemRoleRelationEntity);
    }

}
