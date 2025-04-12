package com.john.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import com.google.common.collect.Lists;
import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceControllerGetClassPathResourceGetDirectoryTest extends BaseTest {

    private URI url;
    private String pathName;

    @Test
    @SneakyThrows
    public void test() {
        var response = this.testRestTemplate.getForEntity(url, String[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(13, response.getHeaders().getContentLength());
        assertEquals(this.pathName, response.getHeaders().getContentDisposition().getFilename());
        assertEquals(1, response.getBody().length);
        assertEquals("email.xml", JinqStream.from(Lists.newArrayList(response.getBody())).getOnlyValue());
        assertEquals("[\"email.xml\"]", this.objectMapper.writeValueAsString(response.getBody()));
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        var relativeUrl = this.storage.storageResource(new ClassPathResource("email/email.xml")).getRelativeUrl();
        this.pathName = JinqStream.from(new URIBuilder(relativeUrl).getPathSegments())
                .skip(1)
                .findFirst()
                .get();
        this.url = new URIBuilder()
                .setPathSegments(JinqStream.from(new URIBuilder(relativeUrl).getPathSegments()).limit(2).toList())
                .build();
    }
}
