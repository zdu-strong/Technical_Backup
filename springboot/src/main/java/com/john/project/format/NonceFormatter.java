package com.john.project.format;

import com.john.project.entity.NonceEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.model.NonceModel;

@Service
public class NonceFormatter extends BaseService {

    public NonceModel format(NonceEntity nonceEntity){
        var nonceModel = new NonceModel();
        BeanUtils.copyProperties(nonceEntity, nonceModel);
        return nonceModel;
    }

}
