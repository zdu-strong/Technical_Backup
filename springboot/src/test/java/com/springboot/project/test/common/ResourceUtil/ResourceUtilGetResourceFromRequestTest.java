package com.springboot.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceUtilGetResourceFromRequestTest extends BaseTest {
    private Resource resource;

    @Test
    public void test() throws IOException {
        var result = this.resourceHttpHeadersUtil.getResourceFromRequest(this.resource.contentLength(), request);
        assertEquals(9287, result.contentLength());
        assertEquals("default.jpg", result.getFilename());
    }

    @BeforeEach
    public void beforeEach() {
        this.resource = new ClassPathResource("image/default.jpg");
        var storageFileModel = this.storage.storageResource(resource);
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
