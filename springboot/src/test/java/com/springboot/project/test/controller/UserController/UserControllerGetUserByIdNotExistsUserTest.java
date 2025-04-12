package com.springboot.project.test.controller.UserController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserControllerGetUserByIdNotExistsUserTest extends BaseTest {

    private String userId;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/user").setParameter("id", userId).build();
        var response = this.testRestTemplate.getForEntity(url, Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User does not exist", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        this.createAccount(email).getId();
        this.userId = Generators.timeBasedReorderedGenerator().generate().toString();
    }

}
