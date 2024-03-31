package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.DistributedExecutionEntity;
import com.springboot.project.model.DistributedExecutionModel;
import com.springboot.project.model.PaginationModel;

@Service
public class DistributedExecutionFormatter extends BaseService {

    public <T> DistributedExecutionModel<T> format(DistributedExecutionEntity distributedExecutionEntity,
            TypeReference<PaginationModel<T>> paginationType) {
        var distributedExecutionModel = new DistributedExecutionModel<T>();
        BeanUtils.copyProperties(distributedExecutionEntity, distributedExecutionModel);
        try {
            distributedExecutionModel.setPagination(
                    this.objectMapper.readValue(distributedExecutionEntity.getPagination(), paginationType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return distributedExecutionModel;
    }

}
