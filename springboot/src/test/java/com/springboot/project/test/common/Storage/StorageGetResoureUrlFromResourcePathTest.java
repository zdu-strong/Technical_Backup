package com.springboot.project.test.common.Storage;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;

import com.springboot.project.test.BaseTest;

public class StorageGetResoureUrlFromResourcePathTest extends BaseTest {
	private String resoucePath;

	@Test
	public void test() throws IOException {
		String resourceUrl = this.storage.getResoureUrlFromResourcePath(resoucePath);
		Assertions.assertNotNull(resourceUrl);
		this.request.setRequestURI(resourceUrl);
		Assertions.assertEquals(9287, this.storage.getResourceFromRequest(request).contentLength());
		Assertions.assertEquals("default.jpg", this.storage.getResourceFromRequest(request).getFilename());
	}

	@BeforeEach
	public void beforeEach() {
		this.resoucePath = this.storage
				.storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg"))).getRelativePath();
	}
}
