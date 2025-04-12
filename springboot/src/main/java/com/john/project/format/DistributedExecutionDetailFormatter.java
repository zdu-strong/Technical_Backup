package com.john.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.DistributedExecutionDetailEntity;
import com.john.project.model.DistributedExecutionMainModel;
import com.john.project.model.DistributedExecutionDetailModel;

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
