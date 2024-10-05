package com.springboot.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceUtilSetContentLengthByRangeTest extends BaseTest {
    private HttpHeaders httpHeaders;
    private Resource resource;

    @Test
    public void test() throws IOException {
        this.resourceHttpHeadersUtil.setContentLength(httpHeaders, resource.contentLength(), request);
        assertEquals(101, this.httpHeaders.getContentLength());
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        this.resource = new ClassPathResource("image/default.jpg");
        var storageFileModel = this.storage.storageResource(this.resource);
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
        this.request.addHeader(HttpHeaders.RANGE, "bytes= 0-100");
    }
}
