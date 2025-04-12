package com.springboot.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.google.common.net.HttpHeaders;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceUtilGetResourceFromRequestByMultipartRangeTest extends BaseTest {
    private Resource resource;

    @Test
    @SneakyThrows
    public void test() {
        var result = this.resourceHttpHeadersUtil.getResourceFromRequest(this.resource.contentLength(), request);
        assertTrue(result.contentLength() > 300);
        assertEquals("default.jpg", result.getFilename());
    }

    @BeforeEach
    public void beforeEach() {
        this.resource = new ClassPathResource("image/default.jpg");
        var storageFileModel = this.storage.storageResource(resource);
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
        this.request.addHeader(HttpHeaders.RANGE, "bytes= 0-99,700-799,2000-2099");
    }
}
