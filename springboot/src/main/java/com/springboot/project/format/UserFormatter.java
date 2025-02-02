package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.UserModel;

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
                .where(s -> s.getRole().getIsActive())
                .select(s -> s.getRole())
                .where(s -> s.getIsActive())
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
                .where(s -> s.getIsActive())
                .map(s -> this.userEmailFormatter.format(s))
                .toList();
        userModel.setUserEmailList(userEmailList);
        return userModel;
    }

}
