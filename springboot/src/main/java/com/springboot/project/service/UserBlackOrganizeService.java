package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.UserBlackOrganizeModel;

@Service
public class UserBlackOrganizeService extends BaseService {

    public UserBlackOrganizeModel create(String userId, String organizeId) {
        var user = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .where(s -> s.getIsActive())
                .getOnlyValue();
        var organize = this.OrganizeEntity()
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var userBlackOrganizeEntity = new UserBlackOrganizeEntity();
        userBlackOrganizeEntity.setId(newId());
        userBlackOrganizeEntity.setCreateDate(new Date());
        userBlackOrganizeEntity.setUpdateDate(new Date());
        userBlackOrganizeEntity.setUser(user);
        userBlackOrganizeEntity.setOrganize(organize);
        this.persist(userBlackOrganizeEntity);

        return this.userBlackOrganizeFormatter.format(userBlackOrganizeEntity);
    }

    public void delete(String id) {
        var userBlackOrganizeEntity = this.UserBlackOrganizeEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        this.remove(userBlackOrganizeEntity);
    }

}
