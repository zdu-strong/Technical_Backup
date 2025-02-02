package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;

@Service
public class RoleOrganizeRelationService extends BaseService {

    public void create(String roleId, String organizeId) {
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var roleEntity = this.streamAll(RoleEntity.class)
                .where(s -> s.getId().equals(roleId))
                .getOnlyValue();

        var roleOrganizeRelationEntity = new RoleOrganizeRelationEntity();
        roleOrganizeRelationEntity.setId(newId());
        roleOrganizeRelationEntity.setCreateDate(new Date());
        roleOrganizeRelationEntity.setUpdateDate(new Date());
        roleOrganizeRelationEntity.setRole(roleEntity);
        roleOrganizeRelationEntity.setOrganize(organizeEntity);
        this.persist(roleOrganizeRelationEntity);
    }

}
