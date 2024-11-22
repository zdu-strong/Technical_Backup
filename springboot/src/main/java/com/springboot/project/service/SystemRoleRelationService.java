package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enumerate.SystemRoleEnum;

@Service
public class SystemRoleRelationService extends BaseService {

    public void create(String userRoleId, SystemRoleEnum systemRoleEnum) {
        var systemRoleName = systemRoleEnum.getRole();
        var systemRoleEntity = this.streamAll(SystemRoleEntity.class)
                .where(s -> s.getName().equals(systemRoleName))
                .getOnlyValue();
        var userRoleEntity = this.streamAll(UserRoleEntity.class)
                .where(s -> s.getId().equals(userRoleId))
                .getOnlyValue();

        var systemRoleRelationEntity = new SystemRoleRelationEntity();
        systemRoleRelationEntity.setId(newId());
        systemRoleRelationEntity.setCreateDate(new Date());
        systemRoleRelationEntity.setUpdateDate(new Date());
        systemRoleRelationEntity.setSystemRole(systemRoleEntity);
        systemRoleRelationEntity.setUserRole(userRoleEntity);
        this.persist(systemRoleRelationEntity);
    }

}
