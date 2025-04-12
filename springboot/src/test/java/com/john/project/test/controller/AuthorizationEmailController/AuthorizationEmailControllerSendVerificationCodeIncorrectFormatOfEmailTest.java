package com.john.project.test.controller.AuthorizationEmailController;

import static org.junit.jupiter.api.Assertions.*;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;

public class AuthorizationEmailControllerSendVerificationCodeIncorrectFormatOfEmailTest extends BaseTest {
    private String email;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/email/send-verification-code").setParameter("email", email).build();
        var response = this.testRestTemplate.postForEntity(url, new HttpEntity<>(null),
                Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid email format", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedReorderedGenerator().generate().toString();
    }

}
