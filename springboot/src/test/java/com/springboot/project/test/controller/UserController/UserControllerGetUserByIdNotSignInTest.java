package com.springboot.project.test.controller.UserController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserControllerGetUserByIdNotSignInTest extends BaseTest {

    private String userId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/user").setParameter("id", userId).build();
        var response = this.testRestTemplate.getForEntity(url, Throwable.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Please login first and then visit", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.userId = Generators.timeBasedReorderedGenerator().generate().toString();
    }

}
