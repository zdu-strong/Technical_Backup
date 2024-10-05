package com.springboot.project.test.controller.AuthorizationEmailController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

import com.fasterxml.uuid.Generators;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationEmailControllerSendVerificationCodeTest extends BaseTest {
    private String email;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var url = new URIBuilder("/email/send_verification_code").setParameter("email", email).build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(null),
                VerificationCodeEmailModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(6, response.getBody().getVerificationCodeLength());
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
    }

}
