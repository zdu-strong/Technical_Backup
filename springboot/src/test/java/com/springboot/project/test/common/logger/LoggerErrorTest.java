package com.springboot.project.test.common.logger;

import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerErrorTest extends BaseTest {

    @Test
    public void test() {
        log.error("Failed", new RuntimeException("Unexpected error"));
    }

}
