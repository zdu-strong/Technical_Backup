package com.john.project.test.service.LoggerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.common.collect.Lists;
import com.john.project.model.LoggerModel;
import com.john.project.test.common.BaseTest.BaseTest;
import ch.qos.logback.classic.Level;

public class LoggerServiceCreateLoggerTest extends BaseTest {

    private LoggerModel loggerModel;

    @Test
    public void test() {
        var result = this.loggerService.create(loggerModel);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(result.getHasException());
        assertEquals("Hello, World!", result.getMessage());
        assertEquals("java.lang.RuntimeException", result.getExceptionClassName());
        assertEquals("Bug", result.getExceptionMessage());
        assertTrue(List.of(73,74).contains(result.getExceptionStackTrace().size()));
        assertEquals("com.springboot.project.controller.HelloWorldController", result.getLoggerName());
        assertEquals(this.gitProperties.getCommitId(), result.getGitCommitId());
        assertEquals(Date.from(this.gitProperties.getCommitTime()), result.getGitCommitDate());
        assertNotNull(result.getCreateDate());
        assertEquals("com.springboot.project.controller.HelloWorldController", result.getCallerClassName());
        assertEquals("helloWorld", result.getCallerMethodName());
        assertEquals(15, result.getCallerLineNumber());
    }

    @BeforeEach
    public void BeforeEach() {
        this.loggerModel = new LoggerModel().setLevel(Level.ERROR.levelStr).setMessage("Hello, World!")
                .setHasException(true)
                .setExceptionClassName("java.lang.RuntimeException")
                .setExceptionMessage("Bug")
                .setExceptionStackTrace(JinqStream.from(Lists.newArrayList(new RuntimeException().getStackTrace()))
                        .select(s -> s.toString())
                        .select(s -> "at " + s)
                        .toList())
                .setLoggerName("com.springboot.project.controller.HelloWorldController")
                .setGitCommitId(this.gitProperties.getCommitId())
                .setGitCommitDate(Date.from(this.gitProperties.getCommitTime()))
                .setCallerClassName("com.springboot.project.controller.HelloWorldController")
                .setCallerMethodName("helloWorld")
                .setCallerLineNumber(15L);
    }

}
