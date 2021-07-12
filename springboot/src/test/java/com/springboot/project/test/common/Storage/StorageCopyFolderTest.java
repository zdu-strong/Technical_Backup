package com.springboot.project.test.common.Storage;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;

import com.springboot.project.test.BaseTest;

public class StorageCopyFolderTest extends BaseTest {
	private File tempFolder;

	@Test
	public void test() {
		var storageFileModel = this.storage.storageResource(new FileSystemResource(tempFolder));
		Assertions.assertEquals(9287,
				FileUtils.sizeOfDirectory(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath())));
		Assertions.assertEquals("default.jpg",
				new File(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()), "default.jpg")
						.getName());
		Assertions.assertEquals(9287,
				new File(this.storage.createTempFileOrFolder(storageFileModel.getRelativePath()), "default.jpg")
						.length());
	}

	@BeforeEach
	public void beforeEach() {
		this.tempFolder = this.storage.createTempFolderByDecompressingZipResource(
				new UrlResource(ClassLoader.getSystemResource("zip/default.zip")));
	}

	@AfterEach
	public void afterEach() {
		this.storage.delete(tempFolder);
	}
}
