package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.DistributedExecutionEntity;
import com.springboot.project.model.DistributedExecutionModel;

@Service
public class DistributedExecutionFormatter extends BaseService {

    public DistributedExecutionModel format(DistributedExecutionEntity distributedExecutionEntity) {
        var distributedExecutionModel = new DistributedExecutionModel();
        BeanUtils.copyProperties(distributedExecutionEntity, distributedExecutionModel);
        return distributedExecutionModel;
    }

}
