package com.springboot.project.common.DistributedExecutionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.enums.DistributedExecutionEnum;
import com.springboot.project.enums.LongTermTaskTypeEnum;
import com.springboot.project.model.DistributedExecutionMainModel;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;
import com.springboot.project.service.DistributedExecutionMainService;
import com.springboot.project.service.LongTermTaskService;

import cn.hutool.core.util.RandomUtil;
import io.reactivex.rxjava3.core.Flowable;

import com.springboot.project.service.DistributedExecutionDetailService;
import lombok.SneakyThrows;

@Component
public class DistributedExecutionUtil {

    @Autowired
    private DistributedExecutionMainService distributedExecutionMainService;

    @Autowired
    private DistributedExecutionDetailService distributedExecutionDetailService;

    @Autowired
    private LongTermTaskUtil longTermTaskUtil;

    @Autowired
    private GitProperties gitProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LongTermTaskService longTermTaskService;

    @SneakyThrows
    public void refreshData(DistributedExecutionEnum distributedExecutionEnum) {
        var distributedExecutionMainModel = this.getDistributedExecution(distributedExecutionEnum);
        if (distributedExecutionMainModel == null) {
            return;
        }
        while (true) {
            var partitionNum = this.getPartitionNum(distributedExecutionMainModel, distributedExecutionEnum);
            if (partitionNum == null) {
                this.distributedExecutionMainService.refreshDistributedExecution(distributedExecutionMainModel.getId());
                break;
            }

            var longTermTaskUniqueKeyModel = this.getLongTermTaskUniqueKeyModelByPartitionNum(partitionNum,
                    distributedExecutionEnum);
            this.longTermTaskUtil.runSkipWhenExists(() -> {
                var pageNum = this.distributedExecutionDetailService
                        .getPageNumByPartitionNum(distributedExecutionMainModel, partitionNum);
                if (pageNum == null) {
                    return;
                }
                while (true) {
                    if (pageNum < 1) {
                        break;
                    }

                    try {
                        distributedExecutionEnum.executeTask(pageNum);
                        this.distributedExecutionDetailService.createByResult(distributedExecutionMainModel.getId(),
                                partitionNum, pageNum);
                    } catch (Throwable e) {
                        this.distributedExecutionDetailService
                                .createByErrorMessage(distributedExecutionMainModel.getId(), partitionNum, pageNum);
                    }
                    pageNum -= distributedExecutionEnum.getMaxNumberOfParallel();
                }
            }, longTermTaskUniqueKeyModel);
        }
    }

    private DistributedExecutionMainModel getDistributedExecution(DistributedExecutionEnum distributedExecutionEnum) {
        {
            var distributedExecutionMainModel = this.distributedExecutionMainService
                    .getLastDistributedExecution(distributedExecutionEnum);
            if (distributedExecutionMainModel != null && !distributedExecutionMainModel.getIsDone()
                    && distributedExecutionEnum
                            .getMaxNumberOfParallel() == (long) distributedExecutionMainModel.getTotalPartition()) {
                return distributedExecutionMainModel;
            } else if (distributedExecutionMainModel != null && distributedExecutionMainModel.getIsDone()
                    && !new Date().after(DateUtils
                            .addMilliseconds(distributedExecutionMainModel.getUpdateDate(),
                                    (int) distributedExecutionEnum.getTheIntervalBetweenTwoExecutions().toMillis()))) {
                return null;
            }
        }

        {
            var list = new ArrayList<DistributedExecutionMainModel>();
            this.longTermTaskUtil.runSkipWhenExists(() -> {
                {
                    var distributedExecutionMainModel = this.distributedExecutionMainService
                            .getLastDistributedExecution(distributedExecutionEnum);
                    if (distributedExecutionMainModel != null && distributedExecutionMainModel.getIsDone()
                            && !new Date().after(DateUtils
                                    .addMilliseconds(distributedExecutionMainModel.getUpdateDate(),
                                            (int) distributedExecutionEnum.getTheIntervalBetweenTwoExecutions()
                                                    .toMillis()))) {
                        return;
                    }
                }
                var distributedExecutionMainModel = this.distributedExecutionMainService
                        .create(distributedExecutionEnum);
                list.add(distributedExecutionMainModel);
            }, new LongTermTaskUniqueKeyModel()
                    .setType(LongTermTaskTypeEnum.CREATE_DISTRIBUTED_EXECUTION_MAIN.getValue())
                    .setUniqueKey(this.gitProperties.getCommitId()));
            return JinqStream.from(list).findOne().orElse(null);
        }
    }

    private Long getPartitionNum(
            DistributedExecutionMainModel distributedExecutionMainModel,
            DistributedExecutionEnum distributedExecutionEnum) {
        var partitionNumList = Flowable.range(1, distributedExecutionMainModel.getTotalPartition().intValue())
                .filter(s -> s <= distributedExecutionMainModel.getTotalPage())
                .toList()
                .blockingGet();

        while (!partitionNumList.isEmpty()) {
            var partitionNum = partitionNumList.get(RandomUtil.randomInt(0, partitionNumList.size()));
            partitionNumList.removeIf(s -> s == partitionNum);

            var pageNum = this.distributedExecutionDetailService.getPageNumByPartitionNum(distributedExecutionMainModel,
                    partitionNum);
            if (pageNum == null) {
                continue;
            }
            var longTermTaskUniqueKeyModel = this.getLongTermTaskUniqueKeyModelByPartitionNum(partitionNum,
                    distributedExecutionEnum);
            if (this.longTermTaskService.findOneNotRunning(List.of(longTermTaskUniqueKeyModel)) == null) {
                continue;
            }
            return (long) partitionNum;
        }
        return null;
    }

    @SneakyThrows
    private LongTermTaskUniqueKeyModel getLongTermTaskUniqueKeyModelByPartitionNum(long partitionNum,
            DistributedExecutionEnum distributedExecutionEnum) {
        return new LongTermTaskUniqueKeyModel()
                .setType(LongTermTaskTypeEnum.DISTRIBUTED_EXECUTION.getValue())
                .setUniqueKey(this.objectMapper
                        .writeValueAsString(new Pair<>(distributedExecutionEnum.getValue(), partitionNum)));
    }

}
