package com.springboot.project.test.common.Storage;

import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class StorageDeleteTest extends BaseTest {
	private File tempFolder;

	@Test
	public void test() {
		this.storage.delete(this.tempFolder);
		Assertions.assertFalse(this.tempFolder.isDirectory());
	}

	@BeforeEach
	private void beforeEach() {
		this.tempFolder = this.storage.createTempFolder();
	}
}
