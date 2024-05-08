package com.springboot.project.service;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.LongTermTaskEntity;

@Service
public class LongTermTaskService extends BaseService {
    public String createLongTermTask() {
        LongTermTaskEntity longTermTaskEntity = new LongTermTaskEntity();
        longTermTaskEntity.setId(newId());
        longTermTaskEntity.setCreateDate(new Date());
        longTermTaskEntity.setUpdateDate(new Date());
        longTermTaskEntity.setIsDone(false);
        longTermTaskEntity.setResult(this.longTermTaskFormatter.formatResult(null));

        this.persist(longTermTaskEntity);
        return longTermTaskEntity.getId();
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

    public ResponseEntity<?> getLongTermTask(String id) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.longTermTaskFormatter.format(longTermTaskEntity);
    }

}
