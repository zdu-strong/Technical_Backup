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
                .setPassword(null)
                .setPrivateKeyOfRSA(null);
        return userModel;
    }

    public UserModel formatWithMoreInformation(UserEntity userEntity) {
        var userModel = this.format(userEntity);
        userModel.setPrivateKeyOfRSA(userEntity.getPrivateKeyOfRSA());
        userModel.setPassword(userEntity.getPassword());
        var id = userEntity.getId();
        var userEmailList = this.UserEmailEntity()
                .where(s -> s.getUser().getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .map(s -> this.userEmailFormatter.format(s))
                .toList();
        userModel.setUserEmailList(userEmailList);
        return userModel;
    }

}
