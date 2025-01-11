package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.NonceModel;

@Service
public class NonceFormatter extends BaseService {

    public NonceModel format(NonceEntity nonceEntity){
        var nonceModel = new NonceModel();
        BeanUtils.copyProperties(nonceEntity, nonceModel);
        return nonceModel;
    }

}
