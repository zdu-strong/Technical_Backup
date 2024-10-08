package com.springboot.project.format;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.LoggerModel;
import ch.qos.logback.classic.Level;
import lombok.SneakyThrows;

@Service
public class LoggerFormatter extends BaseService {

    @SneakyThrows
    public LoggerModel format(LoggerEntity loggerEntity) {
        var loggerModel = new LoggerModel();
        BeanUtils.copyProperties(loggerEntity, loggerModel);
        loggerModel.setLevel(Level.toLevel(loggerEntity.getLevel()).levelStr);
        loggerModel.setExceptionStackTrace(this.objectMapper.readValue(loggerEntity.getExceptionStackTrace(),
                new TypeReference<List<String>>() {
                }));
        return loggerModel;
    }
}
