package com.springboot.project.service;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.StorageSpaceModel;

@Service
public class StorageSpaceService extends BaseService {
	private Duration tempFileSurvivalDuration = Duration.ofDays(1);

	public StorageSpaceModel createStorageSpaceEntity(String folderName) {
		this.checkIsValidFolderName(folderName);
		if (this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName)).findFirst().isPresent()) {
			StorageSpaceEntity storageSpaceEntity = this.StorageSpaceEntity()
					.where(s -> s.getFolderName().equals(folderName)).findFirst().get();
			storageSpaceEntity.setUpdateDate(new Date());
			this.entityManager.merge(storageSpaceEntity);

			return this.storageSpaceFormatter.format(storageSpaceEntity);
		} else {
			StorageSpaceEntity storageSpaceEntity = new StorageSpaceEntity();
			storageSpaceEntity.setId(UUID.randomUUID().toString());
			storageSpaceEntity.setFolderName(folderName);
			storageSpaceEntity.setCreateDate(new Date());
			storageSpaceEntity.setUpdateDate(storageSpaceEntity.getCreateDate());
			this.entityManager.persist(storageSpaceEntity);

			return this.storageSpaceFormatter.format(storageSpaceEntity);
		}
	}

	public void deleteStorageSpaceEntity(String folderName) {
		this.checkIsValidFolderName(folderName);
		if (this.isUsed(folderName)) {
			return;
		}
		for (var storageSpaceEntity : this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName))
				.toList()) {
			this.entityManager.remove(storageSpaceEntity);
		}
		this.storage.delete(folderName);
	}

	public boolean isUsed(String folderName) {
		this.checkIsValidFolderName(folderName);
		if (this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName)).findFirst().isEmpty()) {
			this.createStorageSpaceEntity(folderName);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MILLISECOND, Long.valueOf(0 - this.tempFileSurvivalDuration.toMillis()).intValue());
		Date expireDate = calendar.getTime();
		var isUsed = this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName))
				.where(s -> s.getUpdateDate().before(expireDate))
				.where((s, t) -> t.stream(StorageSpaceEntity.class).where(m -> m.getFolderName().equals(folderName))
						.where(m -> expireDate.before(m.getUpdateDate())).count() == 0)
				.findFirst().isEmpty();
		return isUsed;
	}

	public PaginationModel<StorageSpaceModel> getStorageSpaceListByPagination(int pageNum, int pageSize) {
		var stream = this.StorageSpaceEntity().sortedBy(s -> s.getCreateDate());
		var storageSpacePaginationModel = new PaginationModel<>(pageNum, pageSize, stream,
				(s) -> this.storageSpaceFormatter.format(s));
		return storageSpacePaginationModel;
	}

	private void checkIsValidFolderName(String folderName) {
		if (Strings.isNullOrEmpty(folderName)) {
			throw new RuntimeException("Folder name cannot be empty");
		}
		if (folderName.contains("/") || folderName.contains("\\")) {
			throw new RuntimeException("Folder name is invalid");
		}
		if (Paths.get(folderName).isAbsolute()) {
			throw new RuntimeException("Folder name is invalid");
		}
	}

}
