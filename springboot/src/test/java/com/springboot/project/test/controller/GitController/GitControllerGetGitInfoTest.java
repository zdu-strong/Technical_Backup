package com.springboot.project.test.controller.GitController;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.springboot.project.model.GitPropertiesModel;
import com.springboot.project.test.BaseTest;

public class GitControllerGetGitInfoTest extends BaseTest {
	@Test
	public void test() throws URISyntaxException {
		URI url = new URIBuilder("/git").build();
		ResponseEntity<GitPropertiesModel> response = this.testRestTemplate.getForEntity(url, GitPropertiesModel.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(40, response.getBody().getCommitId().length());
		Assertions.assertNotNull(response.getBody().getCommitDate());
	}
}
