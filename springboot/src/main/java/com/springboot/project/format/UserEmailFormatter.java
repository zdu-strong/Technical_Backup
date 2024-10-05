package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEmailEntity;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;

@Service
public class UserEmailFormatter extends BaseService {

    public UserEmailModel format(UserEmailEntity userEmailEntity) {
        var userEmailModel = new UserEmailModel();
        BeanUtils.copyProperties(userEmailEntity, userEmailModel);
        userEmailModel.setUser(new UserModel().setId(userEmailEntity.getUser().getId()));
        return userEmailModel;
    }
}
