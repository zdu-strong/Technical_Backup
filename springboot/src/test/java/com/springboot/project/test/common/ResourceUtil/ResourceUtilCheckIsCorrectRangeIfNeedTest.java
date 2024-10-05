package com.springboot.project.test.common.ResourceUtil;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.google.common.net.HttpHeaders;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceUtilCheckIsCorrectRangeIfNeedTest extends BaseTest {
    private Resource resource;

    @Test
    public void test() throws IOException {
        this.resourceHttpHeadersUtil.checkIsCorrectRangeIfNeed(this.resource.contentLength(), request);
    }

    @BeforeEach
    public void beforeEach() {
        this.resource = new ClassPathResource("image/default.jpg");
        var storageFileModel = this.storage.storageResource(this.resource);
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
        this.request.addHeader(HttpHeaders.RANGE, "bytes= 0-99,700-799,2000-2099");
    }
}
