package com.springboot.project.test.common.Storage;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class StorageCreateTempFolderTest extends BaseTest {
	@Test
	public void test() {
		File tempFolder = this.storage.createTempFolder();
		Assertions.assertTrue(tempFolder.isDirectory());
		Assertions.assertTrue(tempFolder.getAbsolutePath().replaceAll(Pattern.quote("\\"), "/")
				.startsWith(this.storage.getRootPath()));
		Assertions.assertEquals(36, tempFolder.getName().length());
	}
}
