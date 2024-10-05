package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.model.StorageSpaceModel;

@Service
public class StorageSpaceFormatter extends BaseService {
	public StorageSpaceModel format(StorageSpaceEntity storageSpaceEntity) {
		var storageSpaceModel = new StorageSpaceModel();
        BeanUtils.copyProperties(storageSpaceEntity, storageSpaceModel);
		return storageSpaceModel;
    }
}
