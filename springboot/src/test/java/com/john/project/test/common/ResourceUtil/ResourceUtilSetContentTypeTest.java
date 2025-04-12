package com.john.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.john.project.test.common.BaseTest.BaseTest;

public class ResourceUtilSetContentTypeTest extends BaseTest {
    private HttpHeaders httpHeaders;

    @Test
    public void test() {
        this.resourceHttpHeadersUtil.setContentType(httpHeaders, this.storage.getResourceFromRequest(request), request);
        assertEquals(MediaType.IMAGE_JPEG, this.httpHeaders.getContentType());
    }

    @BeforeEach
    public void beforeEach() {
        httpHeaders = new HttpHeaders();
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
