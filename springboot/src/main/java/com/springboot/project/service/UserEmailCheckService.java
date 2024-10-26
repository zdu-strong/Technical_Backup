package com.springboot.project.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;
import cn.hutool.core.lang.Validator;

@Service
public class UserEmailCheckService extends BaseService {

    public void checkEmailCorrectFormat(String email) {
        if (!Validator.isEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
        }
    }

    public void checkCannotEmptyOfEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
        }
    }

    public void checkEmailIsNotUsed(String email) {
        var isPresent = this.UserEmailEntity()
                .where(s -> s.getEmail().equals(email))
                .where(s -> s.getIsActive())
                .where(s -> s.getUser().getIsActive())
                .exists();
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail " + email + " has bound account");
        }
    }
}
