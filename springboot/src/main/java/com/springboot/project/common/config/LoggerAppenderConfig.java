package com.springboot.project.common.config;
import java.util.Date;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;
import com.google.common.collect.Lists;
import com.springboot.project.model.LoggerModel;
import com.springboot.project.service.LoggerService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import jakarta.annotation.PostConstruct;

@Component
public class LoggerAppenderConfig extends AppenderBase<ILoggingEvent> {

    @Autowired
    private LoggerService loggerService;

    @Autowired
    protected GitProperties gitProperties;

    @Override
    protected void append(ILoggingEvent eventObject) {

        if (Level.ERROR != eventObject.getLevel()) {
            return;
        }

        var loggerModel = new LoggerModel().setLevel(eventObject.getLevel().levelStr)
                .setMessage(eventObject.getMessage())
                .setHasException(false)
                .setExceptionClassName("")
                .setExceptionMessage("").setExceptionStackTrace(Lists.newArrayList())
                .setLoggerName(eventObject.getLoggerName())
                .setGitCommitId(gitProperties.getCommitId())
                .setGitCommitDate(Date.from(gitProperties.getCommitTime()));

        if (StringUtils.isBlank(loggerModel.getMessage())) {
            loggerModel.setMessage("");
        }
        if (eventObject.getThrowableProxy() != null) {
            loggerModel.setHasException(true);
            loggerModel.setExceptionClassName(eventObject.getThrowableProxy().getClassName());
            loggerModel.setExceptionMessage(eventObject.getThrowableProxy().getMessage());
            loggerModel.setExceptionStackTrace(JinqStream
                    .from(Lists.newArrayList(eventObject.getThrowableProxy().getStackTraceElementProxyArray()))
                    .select(s -> s.getSTEAsString()).toList());
            loggerModel.setMessage(eventObject.getThrowableProxy().getMessage());
        }

        var stackTraceElementList = new Throwable().getStackTrace();
        var stackTraceElement = JinqStream.from(Lists.newArrayList(stackTraceElementList)).skip(9)
                .findFirst().get();
        var callerClassName = stackTraceElement.getClassName();
        var callerMethodName = stackTraceElement.getMethodName();
        var callerLineNumber = Integer.valueOf(stackTraceElement.getLineNumber()).longValue();
        loggerModel.setCallerClassName(callerClassName);
        loggerModel.setCallerMethodName(callerMethodName);
        loggerModel.setCallerLineNumber(callerLineNumber);
        Thread.startVirtualThread(() -> {
            try {
                loggerService.createLogger(loggerModel);
            } catch (Throwable e) {
                // do nothing
            }
        });
    }

    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLoggerList().forEach(new Consumer<Logger>() {

            @Override
            public void accept(Logger logger) {
                if (Logger.ROOT_LOGGER_NAME.equals(logger.getName())) {
                    logger.addAppender(LoggerAppenderConfig.this);
                }
            }
        });

        setContext(context);
        start();
    }
}