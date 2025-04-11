package com.springboot.project.test.common.config.LoggerAppenderConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.test.common.BaseTest.BaseTest;

import ch.qos.logback.classic.Level;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerAppenderConfigTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        log.error("Hello, World!", new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Hello, World!"));
        var result = Flowable.interval(1, TimeUnit.MILLISECONDS)
                .map(s -> this.loggerService.searchByPagination(1, 1, ""))
                .filter(s -> !s.getItems().isEmpty())
                .take(1)
                .map(s -> s.getItems().getFirst())
                .blockingSingle();
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertTrue(result.getHasException());
        assertEquals("Hello, World!", result.getMessage());
        assertEquals(this.gitProperties.getCommitId(), result.getGitCommitId());
        assertEquals(Date.from(this.gitProperties.getCommitTime()), result.getGitCommitDate());
        assertEquals(ResponseStatusException.class.getName(), result.getExceptionClassName());
        assertEquals("500 INTERNAL_SERVER_ERROR \"Hello, World!\"", result.getExceptionMessage());
        assertTrue(result.getExceptionStackTrace().size() >= 70);
        assertEquals("test", result.getCallerMethodName());
        assertEquals(24, result.getCallerLineNumber());
        assertEquals(Level.ERROR.levelStr, result.getLevel());
        assertEquals("com.springboot.project.test.common.config.LoggerAppenderConfig.LoggerAppenderConfigTest",
                result.getLoggerName());
        assertEquals("com.springboot.project.test.common.config.LoggerAppenderConfig.LoggerAppenderConfigTest",
                result.getCallerClassName());
    }

}
