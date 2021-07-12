package com.springboot.project.test.common.Storage;

import java.io.File;
import java.io.IOException;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;

import com.springboot.project.test.BaseTest;

public class StorageListRootsTest extends BaseTest {
	private String folderNameOfResource;

	@Test
	public void test() throws IOException {
		var list = this.storage.listRoots().toList().blockingGet();
		Assertions.assertTrue(list.size() > 0);
		Assertions.assertTrue(list.contains(folderNameOfResource));
		for (var folderName : list) {
			Assertions.assertTrue(!Strings.isNullOrEmpty(folderName));
			Assertions.assertTrue(!folderName.contains("/"));
			Assertions.assertTrue(!folderName.contains("\\"));
			Assertions.assertTrue(new File(this.storage.getRootPath(), folderName).isDirectory());
		}
	}

	@BeforeEach
	public void beforeEach() {
		var storageFileModel = this.storage
				.storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
		this.folderNameOfResource = storageFileModel.getFolderName();
		this.request.setRequestURI(storageFileModel.getRelativeUrl());
	}
}
