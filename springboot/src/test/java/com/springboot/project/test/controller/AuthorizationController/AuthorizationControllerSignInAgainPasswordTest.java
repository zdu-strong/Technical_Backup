package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignInAgainPasswordTest extends BaseTest {

    private String username;
    private String passwordPartJsonString;

    @Test
    public void test()
            throws JsonProcessingException, InvalidKeySpecException, URISyntaxException {
        var passwordParameter = this.encryptDecryptService.encryptByPublicKeyOfRSA(this.passwordPartJsonString);
        var url = new URIBuilder("/sign_in/one_time_password").setParameter("username", username)
                .setParameter("password", passwordParameter)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null,
                Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect username or password",
                response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach()
            throws JsonProcessingException, InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        this.username = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(username);
        var secretKeyOfAES = this.encryptDecryptService
                .generateSecretKeyOfAES(username);
        var passwordPartList = List.of(new Date(),
                Generators.timeBasedReorderedGenerator().generate().toString(),
                secretKeyOfAES);
        this.passwordPartJsonString = this.objectMapper.writeValueAsString(passwordPartList);
        var passwordParameter = this.encryptDecryptService.encryptByPublicKeyOfRSA(this.passwordPartJsonString);
        var url = new URIBuilder("/sign_in/one_time_password").setParameter("username", username)
                .setParameter("password", passwordParameter)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
