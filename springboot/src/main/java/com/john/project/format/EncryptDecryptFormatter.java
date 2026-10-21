package com.john.project.format;

import com.john.project.common.baseService.BaseService;
import com.john.project.entity.EncryptDecryptEntity;
import com.john.project.model.EncryptDecryptModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class EncryptDecryptFormatter extends BaseService {

    public EncryptDecryptModel format(EncryptDecryptEntity encryptDecryptEntity) {
        var encryptDecryptModel = new EncryptDecryptModel();
        BeanUtils.copyProperties(encryptDecryptEntity, encryptDecryptModel);
        return encryptDecryptModel;
    }

}
