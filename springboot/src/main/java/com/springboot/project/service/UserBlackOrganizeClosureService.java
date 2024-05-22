package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;

@Service
public class UserBlackOrganizeClosureService extends BaseService {

    public void create(String userId, String organizeId) {
        var user = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .where(s -> s.getIsActive())
                .getOnlyValue();
        var organize = this.OrganizeEntity()
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var userBlackOrganizeClosureEntity = new UserBlackOrganizeClosureEntity();
        userBlackOrganizeClosureEntity.setId(newId());
        userBlackOrganizeClosureEntity.setCreateDate(new Date());
        userBlackOrganizeClosureEntity.setUpdateDate(new Date());
        userBlackOrganizeClosureEntity.setUser(user);
        userBlackOrganizeClosureEntity.setOrganize(organize);
        this.persist(userBlackOrganizeClosureEntity);
    }

    public void delete(String id) {
        var userBlackOrganizeClosureEntity = this.UserBlackOrganizeClosureEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        this.remove(userBlackOrganizeClosureEntity);
    }
}