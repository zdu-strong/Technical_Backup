package com.springboot.project.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.model.VerificationCodeEmailModel;

@RestController
public class AuthorizationEmailController extends BaseController {

    @PostMapping("/email/send_verification_code")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email)
            throws InvalidKeySpecException, NoSuchAlgorithmException, InterruptedException, ParseException {

        if (StringUtils.isBlank(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
        }

        if (!Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$").matcher(email).find()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
        }

        VerificationCodeEmailModel verificationCodeEmailModel = null;
        for (var i = 10; i > 0; i--) {
            var verificationCodeEmailModelTwo = this.verificationCodeEmailService.createVerificationCodeEmail(email);

            var fastDateFormat = FastDateFormat.getInstance(dateFormatProperties.getYearMonthDayHourMinuteSecond(),
                    TimeZone.getTimeZone("UTC"));
            var createDate = fastDateFormat.parse(
                    fastDateFormat.format(DateUtils.addSeconds(verificationCodeEmailModelTwo.getCreateDate(), 1)));
            Thread.sleep(createDate.getTime() - verificationCodeEmailModelTwo.getCreateDate().getTime());

            if (this.verificationCodeEmailService
                    .isFirstOnTheDurationOfVerificationCodeEmail(verificationCodeEmailModelTwo.getId())) {
                verificationCodeEmailModel = verificationCodeEmailModelTwo;
                break;
            }
        }

        if (verificationCodeEmailModel == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Too many verification code requests in a short period of time");
        }

        this.authorizationEmailUtil.sendVerificationCode(email, verificationCodeEmailModel.getVerificationCode());

        return ResponseEntity.ok(verificationCodeEmailModel);
    }

}
