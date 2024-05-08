package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import com.springboot.project.model.UserEmailModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignUpBlankPublicKeyOfRSATest extends BaseTest {

    private String email;
    private String password;
    private UserModel user;

    @Test
    public void test()
            throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException {
        this.user.setPublicKeyOfRSA(null);
        var url = new URIBuilder("/sign_up").build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(this.user),
                Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please set the public key of RSA", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.password = this.email;
        var verificationCodeEmail = sendVerificationCode(email);
        var keyPairOfRSA = this.encryptDecryptService.generateKeyPairOfRSA();
        var userModelOfSignUp = new UserModel();
        userModelOfSignUp.setUsername(email)
                .setUserEmailList(Lists.newArrayList(new UserEmailModel().setEmail(email)
                        .setVerificationCodeEmail(verificationCodeEmail)))
                .setPublicKeyOfRSA(keyPairOfRSA.getPublicKeyOfRSA());
        userModelOfSignUp
                .setPrivateKeyOfRSA(this.encryptDecryptService.encryptByAES(
                        keyPairOfRSA.getPrivateKeyOfRSA(),
                        this.encryptDecryptService.generateSecretKeyOfAES(password + password)));
        var secretKeyOfAES = this.encryptDecryptService
                .generateSecretKeyOfAES(password);
        userModelOfSignUp.setPassword(this.encryptDecryptService.encryptByAES(secretKeyOfAES, secretKeyOfAES));
        this.user = userModelOfSignUp;
    }

}
