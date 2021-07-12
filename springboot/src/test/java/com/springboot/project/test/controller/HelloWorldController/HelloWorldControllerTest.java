package com.springboot.project.test.controller.HelloWorldController;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.springboot.project.test.BaseTest;

public class HelloWorldControllerTest extends BaseTest {
	@Test
	public void test() throws URISyntaxException {
		URI url = new URIBuilder("/").build();
		ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(13, response.getBody().length());
		Assertions.assertEquals("Hello, World!", response.getBody());
	}
}
