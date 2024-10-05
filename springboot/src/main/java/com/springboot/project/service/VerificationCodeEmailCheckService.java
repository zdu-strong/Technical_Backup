package com.springboot.project.service;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.model.VerificationCodeEmailModel;

@Service
public class VerificationCodeEmailCheckService extends BaseService {

    public void checkVerificationCodeEmailHasBeenUsed(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();

        var verificationCodeEmailEntityOptional = this.VerificationCodeEmailEntity().where(s -> s.getId().equals(id))
                .findOne();
        if (!verificationCodeEmailEntityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        var verificationCodeEmailEntity = verificationCodeEmailEntityOptional.get();

        if (!this.verificationCodeEmailService
                .isFirstOnTheDurationOfVerificationCodeEmail(verificationCodeEmailEntity.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        if (verificationCodeEmailEntity.getHasUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        if (!verificationCodeEmailEntity.getEmail().equals(verificationCodeEmailModel.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        {
            var expiredDate = DateUtils.addMinutes(new Date(), 5);

            if (!verificationCodeEmailEntity.getCreateDate().before(expiredDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
            }

        }

        verificationCodeEmailEntity.setHasUsed(true);
        this.merge(verificationCodeEmailEntity);
    }

    public void checkVerificationCodeEmailIsPassed(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();
        var verificationCodeEmailEntity = this.VerificationCodeEmailEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();

        if (!verificationCodeEmailEntity.getVerificationCode()
                .equals(verificationCodeEmailModel.getVerificationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code is wrong! Email: " + verificationCodeEmailModel.getEmail() + ".");
        }

        verificationCodeEmailEntity.setIsPassed(true);
        this.merge(verificationCodeEmailEntity);
    }

}
