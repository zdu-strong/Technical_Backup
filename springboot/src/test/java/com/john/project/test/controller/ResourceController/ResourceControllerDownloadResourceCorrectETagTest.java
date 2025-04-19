package com.john.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import com.google.common.collect.Lists;
import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceControllerDownloadResourceCorrectETagTest extends BaseTest {

    private String resourceUrl;
    private String etag;

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder(resourceUrl).build();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setRange(Lists.newArrayList(new HttpRange() {

            @Override
            public long getRangeStart(long length) {
                return 0;
            }

            @Override
            public long getRangeEnd(long length) {
                return 0;
            }

            @Override
            public String toString() {
                return "0-0";
            }

        }));
        httpHeaders.setIfNoneMatch(this.etag);
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, httpHeaders),
                byte[].class);
        assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode());
        assertNull(response.getHeaders().getContentType());
        assertFalse(response.getHeaders().getContentDisposition().isInline());
        assertEquals("default.jpg", response.getHeaders().getContentDisposition().getFilename());
        assertEquals(StandardCharsets.UTF_8, response.getHeaders().getContentDisposition().getCharset());
        assertNull(response.getBody());
        assertNotNull(response.getHeaders().getETag());
        assertEquals(this.etag, response.getHeaders().getETag());
        assertTrue(response.getHeaders().getETag().startsWith("\""));
        assertTrue(response.getHeaders().getETag().endsWith("\""));
        assertEquals("max-age=604800, no-transform, public, immutable", response.getHeaders().getCacheControl());
        assertNotEquals(1, response.getHeaders().getContentLength());
        assertEquals("bytes 0-0/9287", response.getHeaders().get("Content-Range").stream().findFirst().get());
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.resourceUrl = storageFileModel.getRelativeDownloadUrl();
        URI url = new URIBuilder(resourceUrl).build();
        var response = this.testRestTemplate.getForEntity(url, byte[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        this.etag = response.getHeaders().getETag();
    }
}
