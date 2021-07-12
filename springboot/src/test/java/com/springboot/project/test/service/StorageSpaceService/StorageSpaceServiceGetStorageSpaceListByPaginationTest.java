package com.springboot.project.test.service.StorageSpaceService;

import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class StorageSpaceServiceGetStorageSpaceListByPaginationTest extends BaseTest {
	private String folderName = UUID.randomUUID().toString();

	@Test
	public void test() throws URISyntaxException {
		var result = this.storageSpaceService.getStorageSpaceListByPagination(1, 1);
		Assertions.assertEquals(1, result.getPageNum());
		Assertions.assertEquals(1, result.getPageSize());
		Assertions.assertTrue(result.getTotalRecord() > 0);
		Assertions.assertTrue(result.getTotalPage() > 0);
		Assertions.assertEquals(1, result.getList().size());
	}

	@BeforeEach
	public void beforeEach() {
		this.storageSpaceService.createStorageSpaceEntity(folderName);
	}

	@AfterEach
	public void afterEach() {
		this.storageSpaceService.deleteStorageSpaceEntity(folderName);
	}
}
