package com.john.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.TokenEntity;
import com.john.project.model.TokenModel;
import com.john.project.model.UserModel;

@Service
public class TokenFormatter extends BaseService {

    public TokenModel format(TokenEntity tokenEntity) {
        var tokenModel = new TokenModel();
        BeanUtils.copyProperties(tokenEntity, tokenModel);
        tokenModel.setUser(new UserModel().setId(tokenEntity.getUser().getId()));
        return tokenModel;
    }

}
