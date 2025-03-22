package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class AuthorizationEmailController extends BaseController {

    @PostMapping("/email/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
        this.userEmailService.checkCannotEmptyOfEmail(email);
        this.userEmailService.checkCorrectFormatOfEmail(email);

        var verificationCodeEmailModel = this.authorizationEmailUtil.sendVerificationCode(email);
        return ResponseEntity.ok(verificationCodeEmailModel);
    }

}
