package com.springboot.project.test.common.RangeFileSystemResource;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;

import com.springboot.project.common.storage.RangeFileSystemResource;
import com.springboot.project.test.BaseTest;

public class RangeFileSystemResourceTest extends BaseTest {
	private File tempFile;

	@Test
	public void test() throws IOException {
		var rangeFileSystemResource = new RangeFileSystemResource(tempFile, 800, 5);
		Assertions.assertEquals(5, rangeFileSystemResource.contentLength());
		Assertions.assertEquals("default.jpg", rangeFileSystemResource.getFilename());
	}

	@BeforeEach
	public void beforeEach() {
		this.tempFile = this.storage
				.createTempFileOrFolder(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
	}
}
