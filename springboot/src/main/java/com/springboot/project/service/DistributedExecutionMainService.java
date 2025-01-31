package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.model.DistributedExecutionMainModel;

@Service
public class DistributedExecutionMainService extends BaseService {

    @Transactional(readOnly = true)
    public DistributedExecutionMainModel getDistributedExecutionWithInprogress(
            DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionType = distributedExecutionEnum.getValue();
        var distributedExecutionMainModel = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getExecutionType().equals(distributedExecutionType))
                .where(s -> !s.getIsDone())
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate())
                .findFirst()
                .map(s -> this.distributedExecutionMainFormatter.format(s))
                .orElse(null);
        return distributedExecutionMainModel;
    }

    @Transactional(readOnly = true)
    public DistributedExecutionMainModel getLastDoneDistributedExecution(
            DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionType = distributedExecutionEnum.getValue();
        var distributedExecutionMainModel = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getExecutionType().equals(distributedExecutionType))
                .where(s -> s.getIsDone())
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate())
                .findFirst()
                .map(s -> this.distributedExecutionMainFormatter.format(s))
                .orElse(null);
        return distributedExecutionMainModel;
    }

    public DistributedExecutionMainModel create(
            DistributedExecutionEnum distributedExecutionEnum, long totalRecord) {
        var distributedExecutionMainEntity = new DistributedExecutionMainEntity();
        distributedExecutionMainEntity.setId(newId());
        distributedExecutionMainEntity.setCreateDate(new Date());
        distributedExecutionMainEntity.setUpdateDate(new Date());
        distributedExecutionMainEntity.setExecutionType(distributedExecutionEnum.getValue());
        distributedExecutionMainEntity.setTotalRecord(totalRecord);
        distributedExecutionMainEntity.setIsDone(totalRecord <= 0);
        distributedExecutionMainEntity.setHasError(false);
        this.persist(distributedExecutionMainEntity);

        return this.distributedExecutionMainFormatter.format(distributedExecutionMainEntity);
    }

    public void refreshDistributedExecution(String id) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (distributedExecutionMainEntity.getIsDone()) {
            return;
        }

        var totalRecordOfDistributedExecutionTaskWithDone = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(id))
                .where(s -> s.getIsDone())
                .count();
        if (totalRecordOfDistributedExecutionTaskWithDone < distributedExecutionMainEntity.getTotalRecord()) {
            return;
        }
        var hasError = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(id))
                .where(s -> s.getHasError())
                .exists();
        distributedExecutionMainEntity.setIsDone(true);
        distributedExecutionMainEntity.setHasError(hasError);
        distributedExecutionMainEntity.setUpdateDate(new Date());
        this.merge(distributedExecutionMainEntity);
    }

}
