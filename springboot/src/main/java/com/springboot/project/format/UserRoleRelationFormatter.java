package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserRoleRelationEntity;
import com.springboot.project.model.UserModel;
import com.springboot.project.model.UserRoleRelationModel;

@Service
public class UserRoleRelationFormatter extends BaseService {

    public UserRoleRelationModel format(UserRoleRelationEntity userRoleRelationEntity) {
        var userRoleRelationModel = new UserRoleRelationModel();
        BeanUtils.copyProperties(userRoleRelationEntity, userRoleRelationModel);
        userRoleRelationModel.setUser(new UserModel().setId(userRoleRelationEntity.getUser().getId()));
        if (userRoleRelationEntity.getOrganize() != null) {
            userRoleRelationModel
                    .setOrganize(this.organizeFormatter.format(userRoleRelationEntity.getOrganize()));
        }
        userRoleRelationModel
                .setUserRole(this.userRoleFormatter.format(userRoleRelationEntity.getUserRole()));
        return userRoleRelationModel;
    }

}
