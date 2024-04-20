package com.springboot.project.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.model.UserModel;

@RestController
public class AuthorizationController extends BaseController {

    /**
     * username: email, userId
     * 
     * @param username
     * @param password
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @PostMapping("/sign_in")
    public ResponseEntity<?> signIn(@RequestParam String username, @RequestParam String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException, JsonMappingException, JsonProcessingException {
        this.userCheckService.checkExistAccount(username);
        var userId = this.userService.getUserId(username);
        var accessToken = this.tokenService.generateAccessToken(userId, password);
        var user = this.userService.getUserWithMoreInformation(userId);
        user.setAccessToken(accessToken);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign_out")
    public ResponseEntity<?> signOut() {
        if (this.permissionUtil.isSignIn(request)) {
            var id = this.tokenService.getDecodedJWTOfAccessToken(this.tokenService.getAccessToken(request)).getId();
            if (this.tokenService.hasExistTokenEntity(id)) {
                this.tokenService.deleteTokenEntity(id);
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get_user_info")
    public ResponseEntity<?> getUserInfo() {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);
        var user = this.userService.getUserWithMoreInformation(userId);
        user.setPassword(null);
        user.setPrivateKeyOfRSA(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        if (StringUtils.isBlank(userModel.getPublicKeyOfRSA())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please set the public key of RSA");
        }

        if (StringUtils.isBlank(userModel.getPrivateKeyOfRSA())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please set the public key of RSA");
        }

        if (StringUtils.isBlank(userModel.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in nickname");
        }

        if (userModel.getUsername().trim().length() != userModel.getUsername().length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }

        {
            for (var userEmail : userModel.getUserEmailList()) {
                if (StringUtils.isBlank(userEmail.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
                }

                if (!Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", userEmail.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
                }

                if (StringUtils.isBlank(userEmail.getVerificationCodeEmail().getVerificationCode())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "The verification code of email " + userEmail.getEmail() + " cannot be empty");
                }

                userEmail.getVerificationCodeEmail().setEmail(userEmail.getEmail());

                this.verificationCodeEmailCheckService
                        .checkVerificationCodeEmailHasBeenUsed(userEmail.getVerificationCodeEmail());

                this.verificationCodeEmailCheckService
                        .checkVerificationCodeEmailIsPassed(userEmail.getVerificationCodeEmail());

                this.userEmailCheckService.checkEmailIsNotUsed(userEmail.getEmail());
            }
        }

        var user = this.userService.signUp(userModel);
        user = this.userService.getUserWithMoreInformation(user.getId());
        user.setPassword(null);
        var accessToken = this.tokenService.generateAccessToken(user.getId());
        user.setAccessToken(accessToken);
        return ResponseEntity.ok(user);
    }

}
