package com.john.project.test.controller.HelloWorldController;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.john.project.test.common.BaseTest.BaseTest;

public class HelloWorldControllerTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder("/").build();
        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(13, response.getBody().length());
        assertEquals("Hello, World!", response.getBody());
    }

}
