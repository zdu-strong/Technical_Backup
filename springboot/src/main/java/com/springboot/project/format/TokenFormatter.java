package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.TokenEntity;
import com.springboot.project.model.TokenModel;
import com.springboot.project.model.UserModel;

@Service
public class TokenFormatter extends BaseService {

    public TokenModel format(TokenEntity tokenEntity) {
        var tokenModel = new TokenModel();
        BeanUtils.copyProperties(tokenEntity, tokenModel);
        tokenModel.setUser(new UserModel().setId(tokenEntity.getUser().getId()));
        return tokenModel;
    }

}
