package com.springboot.project.test.service.StorageSpaceService;

import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class StorageSpaceServiceCreateStorageSpaceEntityTest extends BaseTest {
	private String folderName = UUID.randomUUID().toString();

	@Test
	public void test() throws URISyntaxException {
		var storageSpaceModel = this.storageSpaceService.createStorageSpaceEntity(folderName);
		Assertions.assertEquals(36, storageSpaceModel.getFolderName().length());
		Assertions.assertEquals(36, storageSpaceModel.getId().length());
		Assertions.assertNotNull(storageSpaceModel.getCreateDate());
	}

	@AfterEach
	public void afterEach() {
		this.storageSpaceService.deleteStorageSpaceEntity(folderName);
	}
}
