package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationControllerSignOutNotSignInTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var url = new URIBuilder("/sign_out").build();
        var response = this.testRestTemplate.postForEntity(url, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
