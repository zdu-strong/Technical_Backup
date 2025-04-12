package com.john.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceUtilSetContentRangeIfNeedTest extends BaseTest {
    private HttpHeaders httpHeaders;
    private Resource resource;

    @Test
    @SneakyThrows
    public void test() {
        this.resourceHttpHeadersUtil.setContentRangeIfNeed(httpHeaders, resource.contentLength(), request);
        assertEquals("bytes 0-100/9287", httpHeaders.getFirst(HttpHeaders.CONTENT_RANGE));
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
