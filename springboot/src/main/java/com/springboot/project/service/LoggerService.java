package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.LoggerEntity;
import com.springboot.project.model.LoggerModel;
import lombok.SneakyThrows;

@Service
public class LoggerService extends BaseService {

    @SneakyThrows
    public LoggerModel createLogger(LoggerModel loggerModel) {
        var loggerEntity = new LoggerEntity();
        loggerEntity.setId(newId());
        loggerEntity.setCreateDate(new Date());
        loggerEntity.setUpdateDate(new Date());
        loggerEntity.setIsActive(true);
        loggerEntity.setMessage(loggerModel.getMessage());
        loggerEntity.setLevel(loggerModel.getLevel());
        loggerEntity.setLoggerName(loggerModel.getLoggerName());
        loggerEntity.setHasException(loggerModel.getHasException());
        loggerEntity.setExceptionClassName(loggerModel.getExceptionClassName());
        loggerEntity.setExceptionMessage(loggerModel.getExceptionMessage());
        loggerEntity.setExceptionStackTrace(
                this.objectMapper.writeValueAsString(loggerModel.getExceptionStackTrace()));
        loggerEntity.setGitCommitId(loggerModel.getGitCommitId());
        loggerEntity.setGitCommitDate(loggerModel.getGitCommitDate());
        loggerEntity.setCallerClassName(loggerModel.getCallerClassName());
        loggerEntity.setCallerMethodName(loggerModel.getCallerMethodName());
        loggerEntity.setCallerLineNumber(loggerModel.getCallerLineNumber());
        this.persist(loggerEntity);

        return this.loggerFormatter.format(loggerEntity);
    }

}
