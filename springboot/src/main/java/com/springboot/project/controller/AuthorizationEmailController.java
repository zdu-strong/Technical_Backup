package com.springboot.project.controller;

import java.text.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class AuthorizationEmailController extends BaseController {

    @PostMapping("/email/send_verification_code")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email)
            throws ParseException {
        this.userEmailCheckService.checkCannotEmptyOfEmail(email);
        this.userEmailCheckService.checkEmailCorrectFormat(email);

        var verificationCodeEmailModel = this.authorizationEmailUtil.sendVerificationCode(email);
        return ResponseEntity.ok(verificationCodeEmailModel);
    }

}
