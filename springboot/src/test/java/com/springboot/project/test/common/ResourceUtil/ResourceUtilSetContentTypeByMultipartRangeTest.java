package com.springboot.project.test.common.ResourceUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceUtilSetContentTypeByMultipartRangeTest extends BaseTest {
    private HttpHeaders httpHeaders;

    @Test
    public void test() {
        this.resourceHttpHeadersUtil.setContentType(httpHeaders, this.storage.getResourceFromRequest(request), request);
        Assertions
                .assertTrue(this.httpHeaders.getContentType().toString().startsWith("multipart/byteranges;boundary="));
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
        this.request.addHeader(HttpHeaders.RANGE, "bytes= 0-100,400-500");
    }
}
