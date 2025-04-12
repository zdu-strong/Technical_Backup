package com.john.project.service;

import java.util.Date;

import com.john.project.entity.DistributedExecutionDetailEntity;
import com.john.project.entity.DistributedExecutionMainEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.john.project.common.baseService.BaseService;
import com.john.project.entity.*;
import com.john.project.model.DistributedExecutionDetailModel;
import com.john.project.model.DistributedExecutionMainModel;

@Service
public class DistributedExecutionDetailService extends BaseService {

    public DistributedExecutionDetailModel createByResult(String distributedExecutionMainId, long partitionNum, long pageNum) {
        return this.create(distributedExecutionMainId, partitionNum, pageNum, false);
    }

    public DistributedExecutionDetailModel createByErrorMessage(String distributedExecutionMainId, long partitionNum, long pageNum) {
        return this.create(distributedExecutionMainId, partitionNum, pageNum, true);
    }

    @Transactional(readOnly = true)
    public Long getPageNumByPartitionNum(DistributedExecutionMainModel distributedExecutionMainModel,
            long partitionNum) {
        var distributedExecutionMainId = distributedExecutionMainModel.getId();
        if (distributedExecutionMainModel.getTotalPage() < partitionNum) {
        }
        var pageNum = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(distributedExecutionMainId))
                .where(s -> s.getPartitionNum() == partitionNum)
                .sortedBy(s -> s.getPageNum())
                .findFirst()
                .map(s -> s.getPageNum() - distributedExecutionMainModel.getTotalPartition())
                .orElse(null);
        if (pageNum == null) {
            var residue = distributedExecutionMainModel.getTotalPage()
                    % distributedExecutionMainModel.getTotalPartition();
            if (partitionNum <= residue) {
                pageNum = distributedExecutionMainModel.getTotalPage() - residue + partitionNum;
            } else {
                pageNum = distributedExecutionMainModel.getTotalPage() - residue
                        - distributedExecutionMainModel.getTotalPartition() + partitionNum;
            }
        }

        if (pageNum < 1) {
            return null;
        }
        return pageNum;
    }

    private DistributedExecutionDetailModel create(String distributedExecutionMainId, long partitionNum, long pageNum,
            boolean hasError) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(distributedExecutionMainId))
                .getOnlyValue();

        var distributedExecutionDetailEntity = new DistributedExecutionDetailEntity();
        distributedExecutionDetailEntity.setId(newId());
        distributedExecutionDetailEntity.setCreateDate(new Date());
        distributedExecutionDetailEntity.setUpdateDate(new Date());
        distributedExecutionDetailEntity.setPartitionNum(partitionNum);
        distributedExecutionDetailEntity.setPageNum(pageNum);
        distributedExecutionDetailEntity.setHasError(hasError);
        distributedExecutionDetailEntity.setDistributedExecutionMain(distributedExecutionMainEntity);

        this.persist(distributedExecutionDetailEntity);

        return this.distributedExecutionDetailFormatter.format(distributedExecutionDetailEntity);
    }

}
