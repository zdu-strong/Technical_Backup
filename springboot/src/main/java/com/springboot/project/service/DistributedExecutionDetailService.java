package com.springboot.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.DistributedExecutionDetailModel;

@Service
public class DistributedExecutionDetailService extends BaseService {

    @Autowired
    private DistributedExecutionMainService distributedExecutionMainService;

    public DistributedExecutionDetailModel create(
            String distributedExecutionMainId, long pageNum) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(distributedExecutionMainId))
                .getOnlyValue();

        var distributedExecutionDetailEntity = new DistributedExecutionDetailEntity();
        distributedExecutionDetailEntity.setId(newId());
        distributedExecutionDetailEntity.setCreateDate(new Date());
        distributedExecutionDetailEntity.setUpdateDate(new Date());
        distributedExecutionDetailEntity.setPageNum(pageNum);
        distributedExecutionDetailEntity.setIsDone(false);
        distributedExecutionDetailEntity.setHasError(false);
        distributedExecutionDetailEntity.setDistributedExecutionMain(distributedExecutionMainEntity);

        this.persist(distributedExecutionDetailEntity);

        return this.distributedExecutionDetailFormatter.format(distributedExecutionDetailEntity);
    }

    public void refreshUpdateDate(String id) {
        var distributedExecutionDetailEntity = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (distributedExecutionDetailEntity.getIsDone()) {
            return;
        }
        distributedExecutionDetailEntity.setUpdateDate(new Date());
        this.merge(distributedExecutionDetailEntity);
    }

    public void updateByResult(String id) {
        var distributedExecutionDetailEntity = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        distributedExecutionDetailEntity.setUpdateDate(new Date());
        distributedExecutionDetailEntity.setIsDone(true);
        this.merge(distributedExecutionDetailEntity);
    }

    public void updateByErrorMessage(String id) {
        var distributedExecutionDetailEntity = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        distributedExecutionDetailEntity.setUpdateDate(new Date());
        distributedExecutionDetailEntity.setIsDone(true);
        distributedExecutionDetailEntity.setHasError(true);
        this.merge(distributedExecutionDetailEntity);
    }

    public Long getPageNumForExecution(String distributedExecutionMainId) {
        var distributedExecutionMainEntity = this.streamAll(DistributedExecutionMainEntity.class)
                .where(s -> s.getId().equals(distributedExecutionMainId))
                .getOnlyValue();

        if (distributedExecutionMainEntity.getIsDone()) {
            return null;
        }

        {
            var pageNum = this.streamAll(DistributedExecutionDetailEntity.class)
                    .where(s -> s.getDistributedExecutionMain().getId().equals(distributedExecutionMainId))
                    .min(s -> s.getPageNum());
            if (pageNum == null) {
                return distributedExecutionMainEntity.getTotalRecord();
            }

            if (pageNum > 1) {
                return pageNum - 1;
            }
        }

        var totalRecordOfDistributedExecutionTask = this.streamAll(DistributedExecutionDetailEntity.class)
                .where(s -> s.getDistributedExecutionMain().getId().equals(distributedExecutionMainId))
                .count();

        if (totalRecordOfDistributedExecutionTask < distributedExecutionMainEntity.getTotalRecord()) {
            var pageNum = this.getPageNumOfScarceOfDistributedExecutionTask(distributedExecutionMainId, 1L,
                    distributedExecutionMainEntity.getTotalRecord() + 1);
            if (pageNum != null) {
                return pageNum;
            }
        }

        if (totalRecordOfDistributedExecutionTask >= distributedExecutionMainEntity.getTotalRecord()) {
            var now = DateUtils.addMinutes(new Date(), -1);
            var distributedExecutionDetailEntity = this.streamAll(DistributedExecutionDetailEntity.class)
                    .where(s -> s.getDistributedExecutionMain().getId().equals(distributedExecutionMainId))
                    .where(s -> !s.getIsDone())
                    .where(s -> s.getUpdateDate().before(now))
                    .findFirst()
                    .orElse(null);
            if (distributedExecutionDetailEntity != null) {
                var pageNum = distributedExecutionDetailEntity.getPageNum();
                this.remove(distributedExecutionDetailEntity);
                return pageNum;
            }
        }

        this.distributedExecutionMainService.refreshDistributedExecution(distributedExecutionMainId);

        return null;
    }

    /**
     * 
     * @param distributedExecutionMainId
     * @param start                  include start
     * @param end                    not include end
     * @return
     */
    private Long getPageNumOfScarceOfDistributedExecutionTask(String distributedExecutionMainId, long start, long end) {
        if (start < 1) {
            return null;
        }
        if (end < 1) {
            return null;
        }
        if (start >= end) {
            return null;
        }

        var center = new BigDecimal(start + end).divide(new BigDecimal(2), 0, RoundingMode.FLOOR).longValue();
        if (end > center) {
            var countOfTask = this.streamAll(DistributedExecutionDetailEntity.class)
                    .where(s -> s.getDistributedExecutionMain().getId().equals(distributedExecutionMainId))
                    .where(s -> s.getPageNum() >= center)
                    .where(s -> s.getPageNum() < end)
                    .count();
            if (countOfTask < end - center && end - center == 1) {
                return center;
            } else if (countOfTask < end - center) {
                return this.getPageNumOfScarceOfDistributedExecutionTask(distributedExecutionMainId, center, end);
            }
        }
        if (center > start) {
            var countOfTask = this.streamAll(DistributedExecutionDetailEntity.class)
                    .where(s -> s.getDistributedExecutionMain().getId().equals(distributedExecutionMainId))
                    .where(s -> s.getPageNum() >= start)
                    .where(s -> s.getPageNum() < center)
                    .count();
            if (countOfTask < center - start && center - start == 1) {
                return start;
            } else if (countOfTask < center - start) {
                return this.getPageNumOfScarceOfDistributedExecutionTask(distributedExecutionMainId, start, center);
            }
        }
        return null;
    }

}
