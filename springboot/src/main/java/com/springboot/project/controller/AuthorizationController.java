package com.springboot.project.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.model.UserModel;

import lombok.SneakyThrows;

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
    @SneakyThrows
    public ResponseEntity<?> signIn(@RequestParam String username, @RequestParam String password) {
        this.userCheckService.checkExistAccount(username);
        var userId = this.userService.getUserId(username);

        var secretKeyOfAES = this.encryptDecryptService
                .generateSecretKeyOfAES(password);
        var passwordPartList = List.of(new Date(), Generators.timeBasedReorderedGenerator().generate().toString(),
                secretKeyOfAES);
        var passwordPartJsonString = this.objectMapper.writeValueAsString(passwordPartList);
        var passwordParameter = this.encryptDecryptService.encryptByPublicKeyOfRSA(passwordPartJsonString);
        var accessToken = this.tokenService.generateAccessToken(userId, passwordParameter);
        var user = this.userService.getUserWithMoreInformation(userId);
        user.setAccessToken(accessToken);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign_in/one_time_password")
    @SneakyThrows
    public ResponseEntity<?> signInOneTime(@RequestParam String username, @RequestParam String password) {
        this.userCheckService.checkExistAccount(username);
        var userId = this.userService.getUserId(username);
        var accessToken = this.tokenService.generateAccessToken(userId, password);
        var user = this.userService.getUserWithMoreInformation(userId);
        user.setAccessToken(accessToken);
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

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel) {
        this.userCheckService.checkCannotEmptyOfUsername(userModel);
        this.userCheckService.checkValidEmailForSignUp(userModel);
        this.userRoleRelationCheckService.checkUserRoleRelationListMustBeEmpty(userModel);
        this.userRoleRelationCheckService.checkOrganizeRoleRelationListMustBeEmpty(userModel);
        this.userCheckService.checkCannotEmptyOfPassword(userModel);

        var user = this.userService.create(userModel);
        var accessToken = this.tokenService.generateAccessToken(user.getId());
        user.setAccessToken(accessToken);
        return ResponseEntity.ok(user);
    }

}
