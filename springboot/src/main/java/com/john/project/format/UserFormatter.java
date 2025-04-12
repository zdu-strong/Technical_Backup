package com.john.project.format;

import com.john.project.entity.UserEmailEntity;
import com.john.project.entity.UserEntity;
import com.john.project.entity.UserRoleRelationEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.*;
import com.john.project.model.UserModel;

@Service
public class UserFormatter extends BaseService {

    public UserModel format(UserEntity userEntity) {
        var userModel = new UserModel();
        BeanUtils.copyProperties(userEntity, userModel);
        userModel.setUserEmailList(Lists.newArrayList())
                .setPassword(null);
        var userId = userEntity.getId();
        var roleList = this.streamAll(UserRoleRelationEntity.class)
                .where(s -> s.getUser().getId().equals(userId))
                .where(s -> !s.getRole().getIsDeleted())
                .select(s -> s.getRole())
                .map(s -> this.roleFormatter.format(s))
                .toList();
        userModel.setRoleList(roleList);
        return userModel;
    }

    public UserModel formatWithMoreInformation(UserEntity userEntity) {
        var userModel = this.format(userEntity);
        var id = userEntity.getId();
        var userEmailList = this.streamAll(UserEmailEntity.class)
                .where(s -> s.getUser().getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .map(s -> this.userEmailFormatter.format(s))
                .toList();
        userModel.setUserEmailList(userEmailList);
        return userModel;
    }

}
