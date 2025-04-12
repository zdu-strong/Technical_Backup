package com.john.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import lombok.SneakyThrows;
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
import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceControllerGetRangeClassPathResourceTest extends BaseTest {

    private URI url;

    @Test
    public void test() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setRange(Lists.newArrayList(HttpRange.createByteRange(0, 0)));
        var response = this.testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders),
                byte[].class);
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals(1, response.getHeaders().getContentLength());
        assertEquals("email.xml", response.getHeaders().getContentDisposition().getFilename());
        assertEquals(Integer.valueOf(60).byteValue(),
                JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(response.getBody()))).getOnlyValue());
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        this.url = new URIBuilder(
                this.storage.storageResource(new ClassPathResource("email/email.xml")).getRelativeUrl())
                .build();
    }
}
