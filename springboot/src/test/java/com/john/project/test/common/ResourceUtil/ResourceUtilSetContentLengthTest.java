package com.john.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceUtilSetContentLengthTest extends BaseTest {
    private HttpHeaders httpHeaders;
    private Resource resource;

    @Test
    @SneakyThrows
    public void test() {
        this.resourceHttpHeadersUtil.setContentLength(httpHeaders, resource.contentLength(), request);
        assertEquals(9287, this.httpHeaders.getContentLength());
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        this.resource = new ClassPathResource("image/default.jpg");
        var storageFileModel = this.storage.storageResource(this.resource);
        this.request.setRequestURI(storageFileModel.getRelativeUrl());

    }
}
