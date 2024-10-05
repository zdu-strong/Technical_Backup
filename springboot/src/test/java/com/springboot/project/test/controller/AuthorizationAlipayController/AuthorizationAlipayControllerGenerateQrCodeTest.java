package com.springboot.project.test.controller.AuthorizationAlipayController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationAlipayControllerGenerateQrCodeTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var url = new URIBuilder("/sign_in/alipay/generate_qr_code").build();
        var response = this.testRestTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().startsWith("data:image/png;base64,"));
    }

}
