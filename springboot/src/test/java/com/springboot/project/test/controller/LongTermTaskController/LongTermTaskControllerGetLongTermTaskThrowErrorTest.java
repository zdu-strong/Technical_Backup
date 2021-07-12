package com.springboot.project.test.controller.LongTermTaskController;

import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.springboot.project.test.BaseTest;

public class LongTermTaskControllerGetLongTermTaskThrowErrorTest extends BaseTest {
	private String relativeUrl;

	@Test
	public void test() throws URISyntaxException {
		var url = new URIBuilder(relativeUrl).build();
		var result = this.testRestTemplate.getForEntity(url, Throwable.class);
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
		Assertions.assertEquals("Failed due to insufficient funds", result.getBody().getMessage());
	}

	@BeforeEach
	public void beforeEach() {
		this.relativeUrl = this.longTermTaskUtil.run(() -> {
			throw new RuntimeException("Failed due to insufficient funds");
		}).getBody();
	}
}
