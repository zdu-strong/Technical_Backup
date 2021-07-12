package com.springboot.project.test.common.Storage;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.springboot.project.test.BaseTest;

public class StorageGetResourceFromRequestByRangeTest extends BaseTest {

	@Test
	public void test() throws IOException {
		Resource resource = this.storage.getResourceFromRequest(request, 800, 5);
		Assertions.assertEquals(5, resource.contentLength());
		Assertions.assertEquals("default.jpg", resource.getFilename());
	}

	@BeforeEach
	public void beforeEach() {
		var storageFileModel = this.storage
				.storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
		this.request.setRequestURI(storageFileModel.getRelativeUrl());
	}
}
