package com.springboot.project.test.controller.AuthorizationController;

import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.springboot.project.test.BaseTest;

public class AuthorizationControllerTest extends BaseTest {

	@Test
	public void test() throws URISyntaxException {
		var url = new URIBuilder("/sign_in/email/send_verification_code")
				.setParameter("email", this.authorizationEmailProperties.getSenderEmail()).build();
		var response = this.testRestTemplate.postForEntity(url, null, String.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
