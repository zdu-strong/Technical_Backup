package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.project.common.storage.Storage;
import com.springboot.project.service.StorageSpaceService;

import io.reactivex.Observable;

@Component
@EnableScheduling
public class StorageSpaceScheduled {
	@Autowired
	private StorageSpaceService storageSpaceService;

	@Autowired
	private Storage storage;
	private int pageSize = 1;

	@Scheduled(initialDelay = 60 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
	public void scheduled() {
		int totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1, pageSize).getTotalPage();
		for (var pageNum = totalPage; pageNum > 0; pageNum--) {
			for (var storageSpaceModel : this.storageSpaceService.getStorageSpaceListByPagination(totalPage, pageSize)
					.getList()) {
				if (!this.storageSpaceService.isUsed(storageSpaceModel.getFolderName())) {
					this.storageSpaceService.deleteStorageSpaceEntity(storageSpaceModel.getFolderName());
				}
			}
		}
		this.storage.listRoots().concatMap((folderName) -> {
			return Observable.just(folderName).map((s) -> {
				if (!this.storageSpaceService.isUsed(folderName)) {
					this.storageSpaceService.deleteStorageSpaceEntity(folderName);
				}
				return s;
			}).onErrorResumeNext((s) -> {
				return Observable.empty();
			});
		}).blockingSubscribe();
	}
}
