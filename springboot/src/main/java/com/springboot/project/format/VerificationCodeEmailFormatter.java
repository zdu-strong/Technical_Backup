package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.VerificationCodeEmailEntity;
import com.springboot.project.model.VerificationCodeEmailModel;

@Service
public class VerificationCodeEmailFormatter extends BaseService {

    public VerificationCodeEmailModel format(VerificationCodeEmailEntity verificationCodeEmailEntity) {
        var verificationCodeEmailModel = new VerificationCodeEmailModel();
        BeanUtils.copyProperties(verificationCodeEmailEntity, verificationCodeEmailModel);
        verificationCodeEmailModel.setVerificationCodeLength(
                Integer.valueOf(verificationCodeEmailEntity.getVerificationCode().length()).longValue());
        return verificationCodeEmailModel;
    }

}
