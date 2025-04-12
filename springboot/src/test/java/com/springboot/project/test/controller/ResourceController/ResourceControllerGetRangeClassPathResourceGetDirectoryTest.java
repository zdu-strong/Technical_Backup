package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import com.google.common.collect.Lists;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceControllerGetRangeClassPathResourceGetDirectoryTest extends BaseTest {

    private URI url;
    private String pathName;

    @Test
    public void test() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setRange(Lists.newArrayList(HttpRange.createByteRange(0, 10)));
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders),
                byte[].class);
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals(11, response.getHeaders().getContentLength());
        assertEquals(this.pathName, response.getHeaders().getContentDisposition().getFilename());
        assertEquals(11, response.getBody().length);
        assertEquals(Integer.valueOf(91).byteValue(),
                JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(response.getBody()))).findFirst().get());
        assertEquals("[\"email.xml",
                IOUtils.toString(new ByteArrayInputStream(response.getBody()), StandardCharsets.UTF_8));
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
