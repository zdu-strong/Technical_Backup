package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignInOneTimeIncorrectDateTest extends BaseTest {

    private String username;
    private String password;

    @Test
    public void test() throws JsonProcessingException, InvalidKeySpecException,
            NoSuchAlgorithmException, URISyntaxException {
        var secretKeyOfAES = this.encryptDecryptService
                .generateSecretKeyOfAES(password);
        var passwordPartList = List.of(DateUtils.addMinutes(new Date(), 10),
                Generators.timeBasedReorderedGenerator().generate().toString(),
                secretKeyOfAES);
        var passwordPartJsonString = this.objectMapper.writeValueAsString(passwordPartList);
        var passwordParameter = this.encryptDecryptService.encryptByPublicKeyOfRSA(passwordPartJsonString);
        var url = new URIBuilder("/sign_in/one_time_password").setParameter("username", username)
                .setParameter("password", passwordParameter)
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
        this.password = this.username;
    }

}
