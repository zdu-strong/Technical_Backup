package com.springboot.project.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.model.UserModel;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.StrFormatter;

@Service
public class UserCheckService extends BaseService {

    public void checkValidEmailForSignUp(UserModel userModel) {
        for (var userEmail : userModel.getUserEmailList()) {
            if (StringUtils.isBlank(userEmail.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
            }

            if (!Validator.isEmail(userEmail.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
            }

            if (StringUtils.isBlank(userEmail.getVerificationCodeEmail().getVerificationCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        StrFormatter.format("The verification code of email {} cannot be empty", userEmail.getEmail()));
            }

            userEmail.getVerificationCodeEmail().setEmail(userEmail.getEmail());

            this.verificationCodeEmailCheckService
                    .checkVerificationCodeEmailHasBeenUsed(userEmail.getVerificationCodeEmail());

            this.verificationCodeEmailCheckService
                    .checkVerificationCodeEmailIsPassed(userEmail.getVerificationCodeEmail());

            this.userEmailCheckService.checkEmailIsNotUsed(userEmail.getEmail());
        }
    }

    public void checkCannotEmptyOfUsername(UserModel userModel) {
        if (StringUtils.isBlank(userModel.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in nickname");
        }

        if (userModel.getUsername().trim().length() != userModel.getUsername().length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }
    }

    public void checkExistUserById(String id) {
        if (!hasExistsUserId(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
    }

    public void checkExistAccount(String account) {
        if (!hasExistsUserId(account) && !hasExistEmail(account)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect username or password");
        }
    }

    public void checkCannotEmptyOfPassword(UserModel userModel) {
        if (StringUtils.isBlank(userModel.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in password");
        }

        if (userModel.getPassword().trim().length() != userModel.getPassword().length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot start or end with a space");
        }
    }

    private boolean hasExistsUserId(String userId) {
        var exists = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .where(s -> s.getIsActive())
                .exists();
        return exists;
    }

    private boolean hasExistEmail(String email) {
        var exists = this.UserEmailEntity()
                .where(s -> s.getEmail().equals(email))
                .where(s -> s.getIsActive())
                .where(s -> s.getUser().getIsActive())
                .exists();
        return exists;
    }

}
