package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignInOneTimeIncorrectPasswordTest extends BaseTest {

    private String username;
    private String password;

    @Test
    public void test() throws JsonProcessingException, InvalidKeySpecException,
            NoSuchAlgorithmException, URISyntaxException {
        var url = new URIBuilder("/sign-in/one-time-password")
                .setParameter("username", username)
                .setParameter("password", this.encryptDecryptService.encryptByPublicKeyOfRSA(password))
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect username or password", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach()
            throws JsonProcessingException, InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        this.username = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(username);
        this.password = "Incorrect Password";
    }

}
