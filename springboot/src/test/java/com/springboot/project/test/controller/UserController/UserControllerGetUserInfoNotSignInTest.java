package com.springboot.project.test.controller.UserController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserControllerGetUserInfoNotSignInTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var url = new URIBuilder("/get-user-info").build();
        var response = this.testRestTemplate.getForEntity(url, Throwable.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Please login first and then visit", response.getBody().getMessage());
    }

}
