package com.springboot.project.format;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserRoleEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.UserRoleModel;

@Service
public class UserRoleFormatter extends BaseService {

    public UserRoleModel format(UserRoleEntity userRoleEntity) {
        var userRoleModel = new UserRoleModel();
        BeanUtils.copyProperties(userRoleEntity, userRoleModel);
        userRoleModel.setOrganize(Optional.ofNullable(userRoleEntity.getOrganize())
                .map(this.organizeFormatter::format)
                .orElse(new OrganizeModel().setId(StringUtils.EMPTY)));
        var id = userRoleEntity.getId();

        var systemRoleList = this.streamAll(UserRoleEntity.class)
                .where(s -> s.getId().equals(id))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .select(s -> s.getSystemRole())
                .map(s -> this.systemRoleFormatter.format(s))
                .toList();
        userRoleModel.setSystemRoleList(systemRoleList);
        return userRoleModel;
    }

}
