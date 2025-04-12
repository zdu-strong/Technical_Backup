package com.john.project.test.controller.GitController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.john.project.model.GitPropertiesModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class GitControllerGetGitInfoTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder("/git").build();
        ResponseEntity<GitPropertiesModel> response = this.testRestTemplate.getForEntity(url, GitPropertiesModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(40, response.getBody().getCommitId().length());
        assertNotNull(response.getBody().getCommitDate());
    }

}
