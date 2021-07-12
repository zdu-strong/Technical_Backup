package com.springboot.project.test.service.StorageSpaceService;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class StorageSpaceServiceIsUsedTest extends BaseTest {
	private String folderName = UUID.randomUUID().toString();

	@Test
	public void test() {
		var isUsed = this.storageSpaceService.isUsed(folderName);
		Assertions.assertTrue(isUsed);
	}
}
