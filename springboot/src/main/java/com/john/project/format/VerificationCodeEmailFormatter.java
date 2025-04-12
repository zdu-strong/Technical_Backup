package com.john.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.john.project.common.baseService.BaseService;
import com.john.project.entity.VerificationCodeEmailEntity;
import com.john.project.model.VerificationCodeEmailModel;

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
