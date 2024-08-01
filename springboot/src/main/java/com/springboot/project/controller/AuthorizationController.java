package com.springboot.project.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
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
     * @throws JsonProcessingException
     */
    @PostMapping("/sign_in")
    public ResponseEntity<?> signIn(@RequestParam String username, @RequestParam String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException {
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
        this.userCheckService.checkCannotEmptyOfPublicKey(userModel);
        this.userCheckService.checkCannotEmptyOfPrivateKey(userModel);
        this.userCheckService.checkCannotEmptyOfUsername(userModel);
        this.userCheckService.checkValidEmailForSignUp(userModel);

        var user = this.userService.signUp(userModel);
        user = this.userService.getUserWithMoreInformation(user.getId());
        user.setPassword(null);
        var accessToken = this.tokenService.generateAccessToken(user.getId());
        user.setAccessToken(accessToken);
        return ResponseEntity.ok(user);
    }

}
