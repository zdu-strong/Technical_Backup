package com.springboot.project.test.common.ResourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class ResourceUtilGetRangeListTest extends BaseTest {

    @Test
    public void test() throws IOException {
        var rangeList = this.resourceHttpHeadersUtil.getRangeList(request);
        assertEquals(3, rangeList.size());
    }

    @BeforeEach
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
        this.request.addHeader(HttpHeaders.RANGE, "bytes= 0-100,400-500,100-200");
    }
}
