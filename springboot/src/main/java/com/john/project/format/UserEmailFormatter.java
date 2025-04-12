package com.john.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.UserEmailEntity;
import com.john.project.model.UserEmailModel;
import com.john.project.model.UserModel;

@Service
public class UserEmailFormatter extends BaseService {

    public UserEmailModel format(UserEmailEntity userEmailEntity) {
        var userEmailModel = new UserEmailModel();
        BeanUtils.copyProperties(userEmailEntity, userEmailModel);
        userEmailModel.setUser(new UserModel().setId(userEmailEntity.getUser().getId()));
        return userEmailModel;
    }
}
