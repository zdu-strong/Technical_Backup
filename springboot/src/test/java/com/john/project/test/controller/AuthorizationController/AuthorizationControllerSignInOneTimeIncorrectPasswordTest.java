package com.john.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignInOneTimeIncorrectPasswordTest extends BaseTest {

    private String username;
    private String password;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/sign-in/one-time-password")
                .setParameter("username", username)
                .setParameter("password", this.encryptDecryptService.encryptByPublicKeyOfRSA(password))
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect username or password", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        this.username = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(username);
        this.password = "Incorrect Password";
    }

}
