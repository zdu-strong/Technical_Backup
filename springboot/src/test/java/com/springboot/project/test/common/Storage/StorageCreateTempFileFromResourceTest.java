package com.springboot.project.test.common.Storage;

import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.springboot.project.test.BaseTest;

public class StorageCreateTempFileFromResourceTest extends BaseTest {
	private Resource resource;

	@Test
	public void test() {
		File tempFile = this.storage.createTempFileOrFolder(resource);
		Assertions.assertEquals(9287, tempFile.length());
		Assertions.assertEquals("default.jpg", tempFile.getName());
	}

	@BeforeEach
	public void beforeEach() {
		this.resource = new UrlResource(ClassLoader.getSystemResource("image/default.jpg"));
	}
}
