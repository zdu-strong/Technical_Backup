package com.springboot.project.service;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
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
        longTermTaskEntity.setResult(null);

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
        try {
            LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                    .getOnlyValue();
            longTermTaskEntity.setUpdateDate(new Date());
            longTermTaskEntity.setIsDone(true);
            longTermTaskEntity.setResult(this.objectMapper.writeValueAsString(result));
            this.merge(longTermTaskEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateLongTermTaskByErrorMessage(String id, Throwable e) {
        try {
            LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                    .getOnlyValue();
            longTermTaskEntity.setIsDone(true);
            longTermTaskEntity.setUpdateDate(new Date());
            var body = this.objectMapper.readValue(this.longTermTaskFormatter.formatThrowable(e), Object.class);
            var responseEntity = e instanceof ResponseStatusException
                    ? ResponseEntity.status(((ResponseStatusException) e).getStatusCode())
                    : ResponseEntity.internalServerError();
            var text = this.objectMapper.writeValueAsString(responseEntity.body(body));
            longTermTaskEntity.setResult(text);
            this.merge(longTermTaskEntity);
        } catch (JsonProcessingException e1) {
            throw new RuntimeException(e1.getMessage(), e1);
        }
    }

    public ResponseEntity<?> getLongTermTask(String id) {
        LongTermTaskEntity longTermTaskEntity = this.LongTermTaskEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.longTermTaskFormatter.format(longTermTaskEntity);
    }

}
