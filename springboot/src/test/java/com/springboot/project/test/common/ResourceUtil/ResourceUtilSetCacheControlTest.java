package com.springboot.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceUtilSetCacheControlTest extends BaseTest {
    private HttpHeaders httpHeaders;

    @Test
    public void test() throws IOException {
        this.resourceHttpHeadersUtil.setCacheControl(httpHeaders, request);
        assertEquals("max-age=86400, no-transform, public", this.httpHeaders.getCacheControl());
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
