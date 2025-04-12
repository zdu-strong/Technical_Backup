package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.net.URI;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceControllerIsDirectoryOfResourceNotIsDirectoryTest extends BaseTest {

    private String resourceUrl;

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder(resourceUrl).build();
        var response = this.testRestTemplate.getForEntity(url, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.resourceUrl = "/is_directory/" + String.join("/",
                JinqStream.from(new URIBuilder(storageFileModel.getRelativeUrl()).getPathSegments())
                        .toArray(String[]::new));
    }
}
