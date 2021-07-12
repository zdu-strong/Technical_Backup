package com.springboot.project.test.common.Storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.springboot.project.test.BaseTest;

public class StorageCopyResourceTest extends BaseTest {
	private Resource resource;

	@Test
	public void test() {
		var storageFileModel = this.storage.storageResource(this.resource);
		Assertions.assertEquals(9287, this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).length());
		Assertions.assertEquals("default.jpg",
				this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).getName());
	}

	@BeforeEach
	public void beforeEach() {
		this.resource = new UrlResource(ClassLoader.getSystemResource("image/default.jpg"));
	}
}
