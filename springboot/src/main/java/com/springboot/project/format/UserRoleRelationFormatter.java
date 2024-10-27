package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserSystemRoleRelationEntity;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.UserRoleRelationModel;

@Service
public class UserRoleRelationFormatter extends BaseService {

    public UserRoleRelationModel format(UserSystemRoleRelationEntity userSystemRoleRelationEntity) {
        var userRoleRelationModel = new UserRoleRelationModel();
        BeanUtils.copyProperties(userSystemRoleRelationEntity, userRoleRelationModel);
        userRoleRelationModel.setUser(new UserModel().setId(userSystemRoleRelationEntity.getUser().getId()));
        if (userSystemRoleRelationEntity.getOrganize() != null) {
            userRoleRelationModel
                    .setOrganize(this.organizeFormatter.format(userSystemRoleRelationEntity.getOrganize()));
        }
        userRoleRelationModel
                .setSystemRole(this.systemRoleFormatter.format(userSystemRoleRelationEntity.getSystemRole()));
        return userRoleRelationModel;
    }

}
