package com.springboot.project.test.controller.GitController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.springboot.project.model.GitPropertiesModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class GitControllerGetGitInfoTest extends BaseTest {
    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder("/git").build();
        ResponseEntity<GitPropertiesModel> response = this.testRestTemplate.getForEntity(url, GitPropertiesModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(40, response.getBody().getCommitId().length());
        assertNotNull(response.getBody().getCommitDate());
    }
}
