package com.john.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.DistributedExecutionMainEntity;
import com.john.project.model.DistributedExecutionMainModel;

@Service
public class DistributedExecutionMainFormatter extends BaseService {

    public DistributedExecutionMainModel format(DistributedExecutionMainEntity distributedExecutionMainEntity) {
        var distributedExecutionMainModel = new DistributedExecutionMainModel();
        BeanUtils.copyProperties(distributedExecutionMainEntity, distributedExecutionMainModel);
        return distributedExecutionMainModel;
    }

}
