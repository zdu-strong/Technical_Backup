package com.springboot.project.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.constant.LongTermTaskTempWaitDurationConstant;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.enumeration.LongTermTaskTypeEnum;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;

@Service
public class LongTermTaskService extends BaseService {

    public String createLongTermTask() {
        LongTermTaskEntity longTermTaskEntity = new LongTermTaskEntity();
        longTermTaskEntity.setId(newId());
        longTermTaskEntity.setCreateDate(new Date());
        longTermTaskEntity.setUpdateDate(new Date());
        longTermTaskEntity.setIsDone(false);
        longTermTaskEntity.setResult(this.longTermTaskFormatter.formatResult(null));
        longTermTaskEntity.setUniqueKeyJsonString(this.longTermTaskFormatter
                .formatLongTermTaskUniqueKey(
                        new LongTermTaskUniqueKeyModel().setType(LongTermTaskTypeEnum.COMMON.value)
                                .setUniqueKey(Generators.timeBasedReorderedGenerator().generate().toString())));

        this.persist(longTermTaskEntity);
        return longTermTaskEntity.getId();
    }

    public List<String> createLongTermTask(LongTermTaskUniqueKeyModel... longTermTaskUniqueKey) {
        var expiredDate = DateUtils.addMilliseconds(new Date(),
                (int) -LongTermTaskTempWaitDurationConstant.TEMP_TASK_SURVIVAL_DURATION.toMillis());
        var longTermTaskUniqueKeyList = JinqStream.from(Arrays.asList(longTermTaskUniqueKey))
                .select(s -> this.longTermTaskFormatter.formatLongTermTaskUniqueKey(s))
                .sortedBy(s -> s)
                .toList();

        for (var uniqueKeyJsonString : longTermTaskUniqueKeyList) {
            var longTermTaskList = this.streamAll(LongTermTaskEntity.class)
                    .where(s -> s.getUniqueKeyJsonString().equals(uniqueKeyJsonString))
                    .where(s -> s.getUpdateDate().before(expiredDate) || s.getIsDone())
                    .toList();
            for (var longTermTask : longTermTaskList) {
                this.remove(longTermTask);
            }
        }

        var idList = new ArrayList<String>();

        for (var uniqueKeyJsonString : longTermTaskUniqueKeyList) {
            LongTermTaskEntity longTermTaskEntity = new LongTermTaskEntity();
            longTermTaskEntity.setId(newId());
            longTermTaskEntity.setCreateDate(new Date());
            longTermTaskEntity.setUpdateDate(new Date());
            longTermTaskEntity.setIsDone(false);
            longTermTaskEntity.setResult(this.longTermTaskFormatter.formatResult(null));
            longTermTaskEntity.setUniqueKeyJsonString(uniqueKeyJsonString);
            this.persist(longTermTaskEntity);

            idList.add(longTermTaskEntity.getId());
        }

        return idList;
    }

    public void updateLongTermTaskToRefreshUpdateDate(String id) {
        LongTermTaskEntity longTermTaskEntity = this.streamAll(LongTermTaskEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (longTermTaskEntity.getIsDone()) {
            return;
        }
        longTermTaskEntity.setUpdateDate(new Date());
        this.merge(longTermTaskEntity);
    }

    public void updateLongTermTaskByResult(String id, ResponseEntity<?> result) {
        LongTermTaskEntity longTermTaskEntity = this.streamAll(LongTermTaskEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        longTermTaskEntity.setUpdateDate(new Date());
        longTermTaskEntity.setIsDone(true);
        longTermTaskEntity.setResult(this.longTermTaskFormatter.formatResult(result));
        this.merge(longTermTaskEntity);
    }

    public void updateLongTermTaskByErrorMessage(String id, Throwable e) {
        LongTermTaskEntity longTermTaskEntity = this.streamAll(LongTermTaskEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        longTermTaskEntity.setIsDone(true);
        longTermTaskEntity.setUpdateDate(new Date());
        longTermTaskEntity.setResult(this.longTermTaskFormatter.formatThrowable(e));
        this.merge(longTermTaskEntity);
    }

    public void delete(String id) {
        LongTermTaskEntity longTermTaskEntity = this.streamAll(LongTermTaskEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        this.remove(longTermTaskEntity);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getLongTermTask(String id) {
        LongTermTaskEntity longTermTaskEntity = this.streamAll(LongTermTaskEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.longTermTaskFormatter.format(longTermTaskEntity);
    }

    @Transactional(readOnly = true)
    public LongTermTaskUniqueKeyModel findOneNotRunning(List<LongTermTaskUniqueKeyModel> longTermTaskUniqueKeyList) {
        var expiredDate = DateUtils.addMilliseconds(new Date(),
                (int) -LongTermTaskTempWaitDurationConstant.TEMP_TASK_SURVIVAL_DURATION.toMillis());
        for (var longTermTaskUniqueKeyOneList : Lists.partition(longTermTaskUniqueKeyList, 100)) {
            var uniqueKeyJsonStringList = longTermTaskUniqueKeyOneList.stream()
                    .map(this.longTermTaskFormatter::formatLongTermTaskUniqueKey)
                    .distinct()
                    .toList();
            var runningUniqueKeyJsonStringList = this.streamAll(LongTermTaskEntity.class)
                    .where(s -> uniqueKeyJsonStringList.contains(s.getUniqueKeyJsonString()))
                    .where(s -> expiredDate.before(s.getUpdateDate()))
                    .where(s -> !s.getIsDone())
                    .select(s -> s.getUniqueKeyJsonString())
                    .toList();
            if (uniqueKeyJsonStringList.size() == runningUniqueKeyJsonStringList.size()) {
                continue;
            }
            return longTermTaskUniqueKeyOneList.stream()
                    .filter(s -> !runningUniqueKeyJsonStringList
                            .contains(this.longTermTaskFormatter.formatLongTermTaskUniqueKey(s)))
                    .findFirst()
                    .get();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public void checkHasExistById(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        var hasExist = this.streamAll(LongTermTaskEntity.class)
                .where(s -> s.getId().equals(id))
                .exists();
        if (!hasExist) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified task does not exist");
        }
    }

}
