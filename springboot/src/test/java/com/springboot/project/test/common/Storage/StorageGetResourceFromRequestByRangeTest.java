package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageGetResourceFromRequestByRangeTest extends BaseTest {

    @Test
    public void test() throws IOException {
        Resource resource = this.storage.getResourceFromRequest(request, 800, 5);
        assertEquals(5, resource.contentLength());
        assertEquals("default.jpg", this.storage.getFileNameFromResource(resource));
    }

    @BeforeEach
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
