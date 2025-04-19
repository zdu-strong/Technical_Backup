package com.john.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceControllerUploadResourceTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/upload/resource").build();
        var body = new LinkedMultiValueMap<Object, Object>();
        body.add("file", new ClassPathResource("image/default.jpg"));
        var response = this.testRestTemplate.postForEntity(url, body, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().startsWith("/resource/"));
        var result = this.testRestTemplate.getForEntity(new URIBuilder(response.getBody()).build(), byte[].class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, result.getHeaders().getContentType());
        assertTrue(result.getHeaders().getContentDisposition().isInline());
        assertEquals("default.jpg", result.getHeaders().getContentDisposition().getFilename());
        assertEquals(StandardCharsets.UTF_8, result.getHeaders().getContentDisposition().getCharset());
        assertEquals(9287, result.getBody().length);
        assertNotNull(result.getHeaders().getETag());
        assertTrue(result.getHeaders().getETag().startsWith("\""));
        assertTrue(result.getHeaders().getETag().endsWith("\""));
        assertEquals("max-age=604800, no-transform, public, immutable", result.getHeaders().getCacheControl());
        assertEquals(9287, result.getHeaders().getContentLength());
    }
}
