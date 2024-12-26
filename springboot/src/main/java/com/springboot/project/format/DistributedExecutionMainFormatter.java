package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.DistributedExecutionMainEntity;
import com.springboot.project.model.DistributedExecutionMainModel;

@Service
public class DistributedExecutionMainFormatter extends BaseService {

    public DistributedExecutionMainModel format(DistributedExecutionMainEntity distributedExecutionMainEntity) {
        var distributedExecutionMainModel = new DistributedExecutionMainModel();
        BeanUtils.copyProperties(distributedExecutionMainEntity, distributedExecutionMainModel);
        return distributedExecutionMainModel;
    }

}
