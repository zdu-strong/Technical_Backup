package com.springboot.project.test.scheduled.StorageSpaceScheduled;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;

import com.springboot.project.test.BaseTest;

import io.reactivex.Observable;

public class StorageSpaceScheduledScheduledTest extends BaseTest {
	private String folderName;

	@Test
	public void test() {
		this.storageSpaceScheduled.scheduled();
		var totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1, 1).getTotalPage();
		var list = Observable.interval(0, TimeUnit.SECONDS).take(totalPage).concatMap((s) -> {
			var pageNum = s.intValue() + 1;
			return Observable
					.fromIterable(this.storageSpaceService.getStorageSpaceListByPagination(pageNum, 1).getList());
		}).toList().blockingGet();
		Assertions.assertTrue(list.stream().filter(s -> s.getFolderName().equals(folderName)).findFirst().isPresent());
		Assertions.assertEquals(36,
				JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).getOnlyValue().getId().length());
		Assertions.assertEquals(folderName,
				JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).getOnlyValue().getFolderName());
		Assertions.assertNotNull(
				JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).getOnlyValue().getCreateDate());
		Assertions.assertNotNull(
				JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).getOnlyValue().getUpdateDate());
	}

	@BeforeEach
	public void beforeEach() {
		FileUtils.deleteQuietly(new File(this.storage.getRootPath()));
		var storageFileModel = this.storage
				.storageResource(new UrlResource(ClassLoader.getSystemResource("image/default.jpg")));
		this.folderName = storageFileModel.getFolderName();
	}

}
