package com.john.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;

import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceUtilSetETagTest extends BaseTest {
    private HttpHeaders httpHeaders;

    @Test
    public void test() {
        this.resourceHttpHeadersUtil.setETag(httpHeaders, request);
        assertTrue(StringUtils.isNotBlank(httpHeaders.getETag()));
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
