package com.springboot.project.test.common.StorageRootPathUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.BaseTest;

public class StorageRootPathUtilGetTest extends BaseTest {

	@Test
	public void test() {
		String rootPath = this.storage.getRootPath();
		Assertions.assertTrue(rootPath.endsWith("target/storage"));
	}
}
