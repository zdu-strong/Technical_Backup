package com.springboot.project.service;

import java.util.Date;
import java.util.Optional;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.DistributedExecutionTaskEntity;
import com.springboot.project.model.DistributedExecutionTaskModel;

@Service
public class DistributedExecutionTaskService extends BaseService {

    public DistributedExecutionTaskModel create(
            String distributedExecutionId, long pageNum) {
        var distributedExecutionEntity = this.DistributedExecutionEntity()
                .where(s -> s.getId().equals(distributedExecutionId))
                .getOnlyValue();

        var distributedExecutionTaskEntity = new DistributedExecutionTaskEntity();
        distributedExecutionTaskEntity.setId(newId());
        distributedExecutionTaskEntity.setCreateDate(new Date());
        distributedExecutionTaskEntity.setUpdateDate(new Date());
        distributedExecutionTaskEntity.setPageNum(pageNum);
        distributedExecutionTaskEntity.setIsDone(false);
        distributedExecutionTaskEntity.setHasError(false);
        distributedExecutionTaskEntity.setDistributedExecution(distributedExecutionEntity);

        this.persist(distributedExecutionTaskEntity);

        return this.distributedExecutionTaskFormatter.format(distributedExecutionTaskEntity);
    }

    public void refreshUpdateDate(String id) {
        var distributedExecutionTaskEntity = this.DistributedExecutionTaskEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (distributedExecutionTaskEntity.getIsDone()) {
            return;
        }
        distributedExecutionTaskEntity.setUpdateDate(new Date());
        this.merge(distributedExecutionTaskEntity);
    }

    public void updateByResult(String id) {
        var distributedExecutionTaskEntity = this.DistributedExecutionTaskEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        distributedExecutionTaskEntity.setUpdateDate(new Date());
        distributedExecutionTaskEntity.setIsDone(true);
        this.merge(distributedExecutionTaskEntity);

        this.distributedExecutionService.refreshDistributedExecution(distributedExecutionTaskEntity.getDistributedExecution().getId());
    }

    public void updateByErrorMessage(String id) {
        var distributedExecutionTaskEntity = this.DistributedExecutionTaskEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        distributedExecutionTaskEntity.setUpdateDate(new Date());
        distributedExecutionTaskEntity.setIsDone(true);
        distributedExecutionTaskEntity.setHasError(true);
        this.merge(distributedExecutionTaskEntity);

        this.distributedExecutionService.refreshDistributedExecution(distributedExecutionTaskEntity.getDistributedExecution().getId());
    }

    public Long getPageNumForExecution(String distributedExecutionId) {
        this.distributedExecutionService.refreshDistributedExecution(distributedExecutionId);
        var distributedExecutionEntity = this.DistributedExecutionEntity()
                .where(s -> s.getId().equals(distributedExecutionId))
                .getOnlyValue();

        if (distributedExecutionEntity.getTotalRecord() == 0) {
            return null;
        }

        if(distributedExecutionEntity.getIsDone()){
            return null;
        }

        var totalRecordOfDistributedExecutionTask = this.DistributedExecutionTaskEntity()
                .where(s -> s.getDistributedExecution().getId().equals(distributedExecutionId))
                .count();

        if (totalRecordOfDistributedExecutionTask < distributedExecutionEntity.getTotalRecord()) {
            var pageNum = Optional.ofNullable(this.DistributedExecutionTaskEntity()
                    .where(s -> s.getDistributedExecution().getId().equals(distributedExecutionId))
                    .min(s -> s.getPageNum()))
                    .filter(s -> s != null)
                    .filter(s -> s > 1)
                    .map(s -> s - 1)
                    .orElse(distributedExecutionEntity.getTotalRecord());
            if (!this.DistributedExecutionTaskEntity()
                    .where(s -> s.getDistributedExecution().getId().equals(distributedExecutionId))
                    .where(s -> s.getPageNum() == pageNum)
                    .exists()) {
                return pageNum;
            }
        }

        if (totalRecordOfDistributedExecutionTask >= distributedExecutionEntity.getTotalRecord()) {
            var now = DateUtils.addMinutes(new Date(), -1);
            var distributedExecutionTaskEntity = this.DistributedExecutionTaskEntity()
                    .where(s -> s.getDistributedExecution().getId().equals(distributedExecutionId))
                    .where(s -> !s.getIsDone())
                    .where(s -> s.getUpdateDate().before(now))
                    .findFirst()
                    .orElse(null);
            if (distributedExecutionEntity != null) {
                var pageNum = distributedExecutionTaskEntity.getPageNum();
                this.remove(distributedExecutionTaskEntity);
                return pageNum;
            }
        }

        if (totalRecordOfDistributedExecutionTask < distributedExecutionEntity.getTotalRecord()) {
            for (var i = distributedExecutionEntity.getTotalRecord(); i > 0; i--) {
                var pageNum = i;
                if (!this.DistributedExecutionTaskEntity()
                        .where(s -> s.getDistributedExecution().getId().equals(distributedExecutionId))
                        .where(s -> s.getPageNum() == pageNum)
                        .exists()) {
                    return pageNum;
                }
            }
        }

        return null;
    }

}
