package com.john.project.test.controller.AuthorizationEmailController;

import static org.junit.jupiter.api.Assertions.*;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.john.project.model.VerificationCodeEmailModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class AuthorizationEmailControllerSendVerificationCodeTest extends BaseTest {
    private String email;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/email/send-verification-code").setParameter("email", email).build();
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
