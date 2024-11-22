package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.model.DistributedExecutionModel;

@Service
public class DistributedExecutionService extends BaseService {

    @Transactional(readOnly = true)
    public DistributedExecutionModel getLastSuccessDistributedExecution(
            DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionType = distributedExecutionEnum.getExecutionType();
        var distributedExecutionModel = this.streamAll(DistributedExecutionEntity.class)
                .where(s -> s.getExecutionType().equals(distributedExecutionType))
                .where(s -> s.getIsDone())
                .where(s -> !s.getHasError())
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate())
                .findFirst()
                .map(s -> this.distributedExecutionFormatter.format(s))
                .orElse(null);
        return distributedExecutionModel;
    }

    @Transactional(readOnly = true)
    public DistributedExecutionModel getLastDistributedExecution(DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionType = distributedExecutionEnum.getExecutionType();
        var distributedExecutionModel = this.streamAll(DistributedExecutionEntity.class)
                .where(s -> s.getExecutionType().equals(distributedExecutionType))
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate())
                .findFirst()
                .map(s -> this.distributedExecutionFormatter.format(s))
                .orElse(null);
        return distributedExecutionModel;
    }

    public DistributedExecutionModel create(
            DistributedExecutionEnum distributedExecutionEnum, long totalRecord) {
        var distributedExecutionEntity = new DistributedExecutionEntity();
        distributedExecutionEntity.setId(newId());
        distributedExecutionEntity.setCreateDate(new Date());
        distributedExecutionEntity.setUpdateDate(new Date());
        distributedExecutionEntity.setExecutionType(distributedExecutionEnum.getExecutionType());
        distributedExecutionEntity.setTotalRecord(totalRecord);
        distributedExecutionEntity.setIsDone(totalRecord <= 0);
        distributedExecutionEntity.setHasError(false);
        this.persist(distributedExecutionEntity);

        return this.distributedExecutionFormatter.format(distributedExecutionEntity);
    }

    public void refreshDistributedExecution(String id) {
        var distributedExecutionEntity = this.streamAll(DistributedExecutionEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (distributedExecutionEntity.getIsDone()) {
            return;
        }

        var totalRecordOfDistributedExecutionTaskWithDone = this.streamAll(DistributedExecutionTaskEntity.class)
                .where(s -> s.getDistributedExecution().getId().equals(id))
                .where(s -> s.getIsDone())
                .count();
        if (totalRecordOfDistributedExecutionTaskWithDone < distributedExecutionEntity.getTotalRecord()) {
            return;
        }
        var hasError = this.streamAll(DistributedExecutionTaskEntity.class)
                .where(s -> s.getDistributedExecution().getId().equals(id))
                .where(s -> s.getHasError())
                .exists();
        distributedExecutionEntity.setIsDone(true);
        distributedExecutionEntity.setHasError(hasError);
        distributedExecutionEntity.setUpdateDate(new Date());
        this.merge(distributedExecutionEntity);
    }

}
