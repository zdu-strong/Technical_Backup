package com.springboot.project.test.controller.HelloWorldController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class HelloWorldControllerRepeatdNonceTest extends BaseTest {

    private String nonce;

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder("/").build();
        var httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Nonce", nonce);
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(httpHeaders), Throwable.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Duplicate nonce detected", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.nonce = Generators.timeBasedReorderedGenerator().generate().toString();
        URI url = new URIBuilder("/").build();
        var httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Nonce", nonce);
        ResponseEntity<String> response = this.testRestTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(httpHeaders), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(13, response.getBody().length());
        assertEquals("Hello, World!", response.getBody());
    }
}
