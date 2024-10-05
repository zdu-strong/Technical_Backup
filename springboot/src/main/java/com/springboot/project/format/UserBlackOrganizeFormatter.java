package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.UserBlackOrganizeModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.entity.*;

@Service
public class UserBlackOrganizeFormatter extends BaseService {

    public UserBlackOrganizeModel format(UserBlackOrganizeEntity userBlackOrganizeEntity) {
        var userBlackOrganizeModel = new UserBlackOrganizeModel();
        BeanUtils.copyProperties(userBlackOrganizeEntity, userBlackOrganizeModel);
        userBlackOrganizeModel.setUser(new UserModel().setId(userBlackOrganizeEntity.getUser().getId()));
        userBlackOrganizeModel.setOrganize(new OrganizeModel().setId(userBlackOrganizeEntity.getOrganize().getId()));
        return userBlackOrganizeModel;
    }

}
