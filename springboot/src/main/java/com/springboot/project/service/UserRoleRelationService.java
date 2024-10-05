package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserSystemRoleRelationEntity;

@Service
public class UserRoleRelationService extends BaseService {

    public void create(String userId, String systemRoleId) {
        var userEntity = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        var systemRoleEntity = this.SystemRoleEntity()
                .where(s -> s.getId().equals(systemRoleId))
                .getOnlyValue();

        var userSystemRoleRelationEntity = new UserSystemRoleRelationEntity();
        userSystemRoleRelationEntity.setId(newId());
        userSystemRoleRelationEntity.setCreateDate(new Date());
        userSystemRoleRelationEntity.setUpdateDate(new Date());
        userSystemRoleRelationEntity.setUser(userEntity);
        userSystemRoleRelationEntity.setSystemRole(systemRoleEntity);
        this.persist(userSystemRoleRelationEntity);
    }

}
