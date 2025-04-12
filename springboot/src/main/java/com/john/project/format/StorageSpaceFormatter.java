package com.john.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.StorageSpaceEntity;
import com.john.project.model.StorageSpaceModel;

@Service
public class StorageSpaceFormatter extends BaseService {
	public StorageSpaceModel format(StorageSpaceEntity storageSpaceEntity) {
		var storageSpaceModel = new StorageSpaceModel();
        BeanUtils.copyProperties(storageSpaceEntity, storageSpaceModel);
		return storageSpaceModel;
    }
}
