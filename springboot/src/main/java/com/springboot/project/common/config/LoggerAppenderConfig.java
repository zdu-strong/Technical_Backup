package com.springboot.project.common.config;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.google.common.collect.Lists;
import com.springboot.project.model.LoggerModel;
import com.springboot.project.properties.DatabaseJdbcProperties;
import com.springboot.project.service.LoggerService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ReflectUtil;
import jakarta.annotation.PostConstruct;
import static eu.ciechanowiec.sneakyfun.SneakyConsumer.sneaky;

@Component
public class LoggerAppenderConfig extends AppenderBase<ILoggingEvent> {

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private GitProperties gitProperties;

    @Autowired
    private DatabaseJdbcProperties databaseJdbcProperties;

    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isNeedRecord(eventObject)) {
            return;
        }

        var loggerModel = new LoggerModel()
                .setLevel(eventObject.getLevel().levelStr)
                .setMessage(Optional.ofNullable(eventObject.getMessage())
                        .filter(StringUtils::isNotBlank)
                        .orElse(StringUtils.EMPTY))
                .setHasException(false)
                .setExceptionClassName(StringUtils.EMPTY)
                .setExceptionMessage(StringUtils.EMPTY)
                .setExceptionStackTrace(Lists.newArrayList())
                .setLoggerName(eventObject.getLoggerName())
                .setGitCommitId(gitProperties.getCommitId())
                .setGitCommitDate(Date.from(gitProperties.getCommitTime()))
                .setCallerClassName(StringUtils.EMPTY)
                .setCallerMethodName(StringUtils.EMPTY)
                .setCallerLineNumber(1L);
        setCaller(loggerModel, eventObject);
        setException(loggerModel, eventObject);

        Optional.of(CompletableFuture.runAsync(() -> {
            this.loggerService.create(loggerModel);
        }, executor))
                .filter(s -> this.databaseJdbcProperties.getIsSupportParallelWrite())
                .ifPresent(sneaky(s -> {
                    try {
                        s.get();
                    } catch (Throwable e) {
                        // do nothing
                    }
                }));
    }

    @PostConstruct
    public void init() {
        var context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(this);
        setContext(context);
        start();
    }

    private boolean isNeedRecord(ILoggingEvent eventObject) {
        if (Level.ERROR != eventObject.getLevel()) {
            return false;
        }

        if (eventObject.getThrowableProxy() != null) {
            if (Optional.of(eventObject.getThrowableProxy())
                    .filter(s -> eventObject.getThrowableProxy().getClassName()
                            .equals(ResponseStatusException.class.getName()))
                    .map(s -> ReflectUtil.getFieldValue(eventObject.getThrowableProxy(),
                            "throwable"))
                    .filter(s -> s instanceof ResponseStatusException)
                    .map(s -> (ResponseStatusException) s)
                    .map(s -> s.getStatusCode())
                    .filter(s -> !s.is5xxServerError())
                    .isPresent()) {
                return false;
            }
        }

        return true;
    }

    private void setException(LoggerModel loggerModel, ILoggingEvent eventObject) {
        if (eventObject.getThrowableProxy() != null) {
            loggerModel.setHasException(true);
            loggerModel.setExceptionClassName(eventObject.getThrowableProxy().getClassName());
            loggerModel.setExceptionMessage(eventObject.getThrowableProxy().getMessage());
            setExceptionStackTrace(loggerModel, eventObject.getThrowableProxy());
            if (StringUtils.isBlank(loggerModel.getMessage())
                    && StringUtils.isNotBlank(eventObject.getThrowableProxy().getMessage())) {
                loggerModel.setMessage(eventObject.getThrowableProxy().getMessage());
            }
        }
    }

    private void setCaller(LoggerModel loggerModel, ILoggingEvent eventObject) {
        if (ArrayUtils.isEmpty(eventObject.getCallerData())) {
            return;
        }
        var callData = eventObject.getCallerData()[0];
        loggerModel.setCallerClassName(callData.getClassName());
        loggerModel.setCallerMethodName(callData.getMethodName());
        loggerModel.setCallerLineNumber((long) callData.getLineNumber());
    }

    private void setExceptionStackTrace(LoggerModel loggerModel, IThrowableProxy nextError) {
        while (nextError != null) {
            loggerModel.getExceptionStackTrace().add(
                    StrFormatter.format("{}{}: {}",
                            loggerModel.getExceptionStackTrace().isEmpty() ? StringUtils.EMPTY : "Caused by: ",
                            nextError.getClassName(), nextError.getMessage()));
            loggerModel.getExceptionStackTrace().addAll(JinqStream
                    .from(Lists.newArrayList(nextError.getStackTraceElementProxyArray()))
                    .select(s -> s.getSTEAsString()).toList());
            nextError = nextError.getCause();
        }
    }

}