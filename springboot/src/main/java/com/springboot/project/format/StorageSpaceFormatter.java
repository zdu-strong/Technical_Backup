package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.model.StorageSpaceModel;
import com.springboot.project.service.BaseService;

@Service
public class StorageSpaceFormatter extends BaseService {
	public StorageSpaceModel format(StorageSpaceEntity storageSpaceEntity) {
		var storageSpaceModel = new StorageSpaceModel().setId(storageSpaceEntity.getId())
				.setCreateDate(storageSpaceEntity.getCreateDate()).setUpdateDate(storageSpaceEntity.getUpdateDate())
				.setFolderName(storageSpaceEntity.getFolderName());
		return storageSpaceModel;
	}
}
