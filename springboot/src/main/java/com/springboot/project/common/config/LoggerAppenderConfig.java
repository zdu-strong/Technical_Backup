package com.springboot.project.common.config;

import java.util.Date;
import java.util.concurrent.TimeUnit;
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
import com.springboot.project.service.LoggerService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ReflectUtil;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import jakarta.annotation.PostConstruct;

@Component
public class LoggerAppenderConfig extends AppenderBase<ILoggingEvent> {

    @Autowired
    private LoggerService loggerService;

    @Autowired
    protected GitProperties gitProperties;

    private final PublishProcessor<LoggerModel> subject = PublishProcessor.create();

    @Override
    protected void append(ILoggingEvent eventObject) {

        if (Level.ERROR != eventObject.getLevel()) {
            return;
        }

        var loggerModel = new LoggerModel().setLevel(eventObject.getLevel().levelStr)
                .setMessage(eventObject.getMessage())
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

        if (StringUtils.isBlank(loggerModel.getMessage())) {
            loggerModel.setMessage(StringUtils.EMPTY);
        }
        if (eventObject.getThrowableProxy() != null) {
            loggerModel.setHasException(true);
            if (StringUtils.isBlank(loggerModel.getMessage())) {
                loggerModel.setMessage(eventObject.getThrowableProxy().getMessage());
            }
            loggerModel.setExceptionClassName(eventObject.getThrowableProxy().getClassName());
            loggerModel.setExceptionMessage(eventObject.getThrowableProxy().getMessage());
            setExceptionStackTrace(loggerModel, eventObject.getThrowableProxy());
            if (loggerModel.getExceptionClassName().equals(ResponseStatusException.class.getName())
                    && ReflectUtil.getFieldValue(eventObject.getThrowableProxy(),
                            "throwable") instanceof ResponseStatusException
                    && !((ResponseStatusException) ReflectUtil.getFieldValue(eventObject.getThrowableProxy(),
                            "throwable")).getStatusCode()
                            .is5xxServerError()) {
                return;
            }
        }

        setCaller(loggerModel, eventObject);
        subject.onNext(loggerModel);
    }

    @PostConstruct
    public void init() {
        subject.delay(1, TimeUnit.MILLISECONDS)
                .concatMap((loggerModel) -> {
                    return Flowable.just(StringUtils.EMPTY)
                            .map((s) -> loggerService.createLogger(loggerModel))
                            .onErrorComplete();
                })
                .retry()
                .subscribe();
        var context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(this);
        setContext(context);
        start();
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
        loggerModel.getExceptionStackTrace().add(
                StrFormatter.format("{}{}: {}",
                        loggerModel.getExceptionStackTrace().isEmpty() ? StringUtils.EMPTY : "Caused by: ",
                        nextError.getClassName(), nextError.getMessage()));
        loggerModel.getExceptionStackTrace().addAll(JinqStream
                .from(Lists.newArrayList(nextError.getStackTraceElementProxyArray()))
                .select(s -> s.getSTEAsString()).toList());
        var cause = nextError.getCause();
        if (cause != null) {
            setExceptionStackTrace(loggerModel, cause);
        }
    }
}