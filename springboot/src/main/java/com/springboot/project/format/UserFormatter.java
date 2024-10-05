package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEntity;
import com.springboot.project.model.UserModel;

@Service
public class UserFormatter extends BaseService {

    public UserModel format(UserEntity userEntity) {
        var userModel = new UserModel();
        BeanUtils.copyProperties(userEntity, userModel);
        userModel.setUserEmailList(Lists.newArrayList())
                .setPassword(null);
        var userId = userEntity.getId();
        var systemRoleList = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .selectAllList(s -> s.getUserSystemRoleRelationList())
                .where(s -> s.getOrganize() == null)
                .where(s -> s.getSystemRole().getOrganize() == null)
                .distinct()
                .map(s -> this.userRoleRelationFormatter.format(s))
                .toList();
        userModel.setUserRoleRelationList(systemRoleList);
        var organizeRoleList = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .selectAllList(s -> s.getUserSystemRoleRelationList())
                .where(s -> s.getOrganize() != null)
                .where(s -> s.getSystemRole().getOrganize() != null)
                .map(s -> this.userRoleRelationFormatter.format(s))
                .toList();
        userModel.setOrganizeRoleRelationList(organizeRoleList);
        return userModel;
    }

    public UserModel formatWithMoreInformation(UserEntity userEntity) {
        var userModel = this.format(userEntity);
        var id = userEntity.getId();
        var userEmailList = this.UserEmailEntity()
                .where(s -> s.getUser().getId().equals(id))
                .where(s -> s.getIsActive())
                .map(s -> this.userEmailFormatter.format(s))
                .toList();
        userModel.setUserEmailList(userEmailList);
        return userModel;
    }

}
