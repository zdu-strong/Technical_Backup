package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.DistributedExecutionTaskEntity;
import com.springboot.project.model.DistributedExecutionModel;
import com.springboot.project.model.DistributedExecutionTaskModel;

@Service
public class DistributedExecutionTaskFormatter extends BaseService {

    public DistributedExecutionTaskModel format(DistributedExecutionTaskEntity distributedExecutionTaskEntity) {
        var distributedExecutionTaskModel = new DistributedExecutionTaskModel();
        BeanUtils.copyProperties(distributedExecutionTaskEntity, distributedExecutionTaskModel);
        distributedExecutionTaskModel.setDistributedExecutionModel(new DistributedExecutionModel()
                .setId(distributedExecutionTaskEntity.getDistributedExecution().getId()));
        return distributedExecutionTaskModel;
    }

}
