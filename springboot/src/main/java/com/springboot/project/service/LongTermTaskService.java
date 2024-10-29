package com.springboot.project.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.LongTermTaskEntity;
import com.springboot.project.enumerate.LongTermTaskTypeEnum;
import com.springboot.project.enumerate.LongTermTaskTempWaitDurationEnum;
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
                        new LongTermTaskUniqueKeyModel().setType(LongTermTaskTypeEnum.COMMON.getType())
                                .setUniqueKey(Generators.timeBasedReorderedGenerator().generate().toString())));

        this.persist(longTermTaskEntity);
        return longTermTaskEntity.getId();
    }

    public List<String> createLongTermTask(LongTermTaskUniqueKeyModel... longTermTaskUniqueKey) {
        var expiredDate = DateUtils.addMilliseconds(new Date(),
                (int) -LongTermTaskTempWaitDurationEnum.TEMP_TASK_SURVIVAL_DURATION.toMillis());
        var longTermTaskUniqueKeyList = JinqStream.from(Arrays.asList(longTermTaskUniqueKey))
                .select(s -> this.longTermTaskFormatter.formatLongTermTaskUniqueKey(s))
                .sortedBy(s -> s)
                .toList();

        for (var uniqueKeyJsonString : longTermTaskUniqueKeyList) {
            var longTermTaskList = this.LongTermTaskEntity()
                    .where(s -> s.getUniqueKeyJsonString().equals(uniqueKeyJsonString))
                    .where(s -> s.getUpdateDate().before(expiredDate))
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
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (longTermTaskEntity.getIsDone()) {
            return;
        }
        longTermTaskEntity.setUpdateDate(new Date());
        this.merge(longTermTaskEntity);
    }

    public void updateLongTermTaskByResult(String id, ResponseEntity<?> result) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        longTermTaskEntity.setUpdateDate(new Date());
        longTermTaskEntity.setIsDone(true);
        longTermTaskEntity.setResult(this.longTermTaskFormatter.formatResult(result));
        this.merge(longTermTaskEntity);
    }

    public void updateLongTermTaskByErrorMessage(String id, Throwable e) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        longTermTaskEntity.setIsDone(true);
        longTermTaskEntity.setUpdateDate(new Date());
        longTermTaskEntity.setResult(this.longTermTaskFormatter.formatThrowable(e));
        this.merge(longTermTaskEntity);
    }

    public void delete(String id) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        this.remove(longTermTaskEntity);
    }

    public ResponseEntity<?> getLongTermTask(String id) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.longTermTaskFormatter.format(longTermTaskEntity);
    }

    public void checkIsExistLongTermTaskById(String id) {
        var isExistLongTermTask = this.LongTermTaskEntity().where(s -> s.getId().equals(id)).exists();
        if (!isExistLongTermTask) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified task does not exist");
        }
    }

}
