package com.springboot.project.test.controller.AuthorizationAlipayController;

import static org.junit.jupiter.api.Assertions.*;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class AuthorizationAlipayControllerGenerateQrCodeTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/sign-in/alipay/generate-qr-code").build();
        var response = this.testRestTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().startsWith("data:image/png;base64,"));
    }

}
