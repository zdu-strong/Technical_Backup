package com.springboot.project.test.common.SequenceResource;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import com.google.common.collect.Lists;
import com.springboot.project.common.storage.RangeFileSystemResource;
import com.springboot.project.common.storage.SequenceResource;
import com.springboot.project.test.BaseTest;

public class SequenceResourceTest extends BaseTest {
	private File tempFile;

	@Test
	public void test() throws IOException {
		var sequenceResource = new SequenceResource("default.jpg", Lists.newArrayList(
				new RangeFileSystemResource(tempFile, 800, 5), new RangeFileSystemResource(tempFile, 805, 6)));
		Assertions.assertEquals(11, sequenceResource.contentLength());
		Assertions.assertEquals("default.jpg", sequenceResource.getFilename());
	}

	@BeforeEach
	public void beforeEach() {
		this.tempFile = this.storage
				.createTempFileOrFolder(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
	}
}
