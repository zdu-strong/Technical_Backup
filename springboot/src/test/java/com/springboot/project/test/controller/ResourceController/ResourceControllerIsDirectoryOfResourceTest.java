package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceControllerIsDirectoryOfResourceTest extends BaseTest {

    private String resourceUrl;

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder(resourceUrl).build();
        var response = this.testRestTemplate.getForEntity(url, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.resourceUrl = "/is_directory/" + String.join("/",
                JinqStream.from(new URIBuilder(storageFileModel.getRelativeUrl()).getPathSegments()).limit(2)
                        .toArray(String[]::new));
    }
}
