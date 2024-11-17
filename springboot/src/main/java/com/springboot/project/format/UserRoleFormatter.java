package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserRoleEntity;
import com.springboot.project.model.UserRoleModel;

@Service
public class UserRoleFormatter extends BaseService {

    public UserRoleModel format(UserRoleEntity userRoleEntity) {
        var userRoleModel = new UserRoleModel();
        BeanUtils.copyProperties(userRoleEntity, userRoleModel);
        if (userRoleEntity.getOrganize() != null) {
            userRoleModel.setOrganize(this.organizeFormatter.format(userRoleEntity.getOrganize()));
        }
        var id = userRoleEntity.getId();

        var systemRoleList = this.UserRoleEntity()
                .where(s -> s.getId().equals(id))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .select(s -> s.getSystemRole())
                .map(s -> this.systemRoleFormatter.format(s))
                .toList();
        userRoleModel.setSystemRoleList(systemRoleList);
        return userRoleModel;
    }

}
