package com.john.project.test.common.logger;

import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerInfoTest extends BaseTest {

    @Test
    public void test() {
        log.info("This is a message");
    }

}
