package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.lang.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ValidationFieldUtilCorrectFormat extends ValidationFieldUtilNotEdgesSpace {

    public void checkCorrectFormatOfEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return;
        }
        if (!Validator.isEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
    }

}
