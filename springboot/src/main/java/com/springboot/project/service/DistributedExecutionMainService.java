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
    public DistributedExecutionMainModel getLastDistributedExecution(
            DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionType = distributedExecutionEnum.getValue();
        var distributedExecutionMainModel = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getExecutionType().equals(distributedExecutionType))
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate())
                .findFirst()
                .map(s -> this.distributedExecutionMainFormatter.format(s))
                .orElse(null);
        return distributedExecutionMainModel;
    }

    public DistributedExecutionMainModel create(DistributedExecutionEnum distributedExecutionEnum) {
        {
            var distributedExecutionMainList = this.streamAll(DistributedExecutionMainEntity.class)
                    .where(s -> !s.getIsDone())
                    .toList();
            for (var distributedExecutionMainEntity : distributedExecutionMainList) {
                if (distributedExecutionMainEntity.getTotalPartition() == distributedExecutionEnum
                        .getMaxNumberOfParallel()) {
                    return this.distributedExecutionMainFormatter.format(distributedExecutionMainEntity);
                } else {
                    this.remove(distributedExecutionMainEntity);
                }
            }
        }

        var distributedExecutionMainEntity = new DistributedExecutionMainEntity();
        distributedExecutionMainEntity.setId(newId());
        distributedExecutionMainEntity.setCreateDate(new Date());
        distributedExecutionMainEntity.setUpdateDate(new Date());
        distributedExecutionMainEntity.setExecutionType(distributedExecutionEnum.getValue());
        distributedExecutionMainEntity.setTotalPage(distributedExecutionEnum.getTotalPage());
        distributedExecutionMainEntity.setTotalPartition(distributedExecutionEnum.getMaxNumberOfParallel());
        distributedExecutionMainEntity.setIsDone(distributedExecutionMainEntity.getTotalPage() <= 0);
        distributedExecutionMainEntity.setHasError(false);
        distributedExecutionMainEntity.setTotalPartition(distributedExecutionEnum.getMaxNumberOfParallel());
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

        var totalPageOfDistributedExecutionTaskWithDone = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(id))
                .where(s -> s.getIsDone())
                .count();
        if (totalPageOfDistributedExecutionTaskWithDone < distributedExecutionMainEntity.getTotalPage()) {
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
