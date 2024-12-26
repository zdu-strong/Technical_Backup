package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.DistributedExecutionDetailEntity;
import com.springboot.project.model.DistributedExecutionMainModel;
import com.springboot.project.model.DistributedExecutionDetailModel;

@Service
public class DistributedExecutionDetailFormatter extends BaseService {

    public DistributedExecutionDetailModel format(DistributedExecutionDetailEntity distributedExecutionDetailEntity) {
        var distributedExecutionDetailModel = new DistributedExecutionDetailModel();
        BeanUtils.copyProperties(distributedExecutionDetailEntity, distributedExecutionDetailModel);
        distributedExecutionDetailModel.setDistributedExecutionMain(new DistributedExecutionMainModel()
                .setId(distributedExecutionDetailEntity.getDistributedExecutionMain().getId()));
        return distributedExecutionDetailModel;
    }

}
