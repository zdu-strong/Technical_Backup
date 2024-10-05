package com.springboot.project.test.controller.ResourceController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import com.google.common.collect.Lists;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceControllerGetClassPathResourceTest extends BaseTest {

    private URI url;

    @Test
    public void test() throws IOException {
        var response = this.testRestTemplate.getForEntity(url, byte[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Lists.newArrayList(57L, 59L).contains(response.getHeaders().getContentLength()));
        assertEquals("email.xml", response.getHeaders().getContentDisposition().getFilename());
        assertEquals(Integer.valueOf(60).byteValue(),
                JinqStream.from(Lists.newArrayList(ArrayUtils.toObject(response.getBody()))).findFirst().get());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.url = new URIBuilder(
                this.storage.storageResource(new ClassPathResource("email/email.xml")).getRelativeUrl()).build();
    }
}
