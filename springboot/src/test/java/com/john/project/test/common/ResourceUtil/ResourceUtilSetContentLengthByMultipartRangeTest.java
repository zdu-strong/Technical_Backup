package com.john.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceUtilSetContentLengthByMultipartRangeTest extends BaseTest {
    private HttpHeaders httpHeaders;
    private Resource resource;

    @Test
    @SneakyThrows
    public void test() {
        this.resourceHttpHeadersUtil.setContentLength(httpHeaders, resource.contentLength(), request);
        assertEquals(9287, resource.contentLength());
        assertTrue(this.httpHeaders.getContentLength() > 300);
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        var resource = new ClassPathResource("image/default.jpg");
        var storageFileModel = this.storage.storageResource(resource);
        var request = new MockHttpServletRequest();
        request.setRequestURI(storageFileModel.getRelativeUrl());
        this.resource = this.storage.getResourceFromRequest(request);
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
        this.request.addHeader(HttpHeaders.RANGE, "bytes= 0-99,400-499,1900-1999");
    }
}
