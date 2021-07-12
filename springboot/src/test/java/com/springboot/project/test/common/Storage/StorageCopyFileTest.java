package com.springboot.project.test.common.Storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;

import com.springboot.project.test.BaseTest;

public class StorageCopyFileTest extends BaseTest {

	@Test
	public void test() {
		var storageFileModel = this.storage
				.storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
		Assertions.assertEquals(9287, this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).length());
		Assertions.assertEquals("default.jpg",
				this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).getName());
		Assertions.assertFalse(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).isDirectory());
		Assertions.assertTrue(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()).isFile());
	}
}
