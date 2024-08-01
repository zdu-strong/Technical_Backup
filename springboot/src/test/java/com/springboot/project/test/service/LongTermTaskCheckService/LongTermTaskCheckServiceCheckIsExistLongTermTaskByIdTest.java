package com.springboot.project.test.service.LongTermTaskCheckService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskCheckServiceCheckIsExistLongTermTaskByIdTest extends BaseTest {

    private String longTermtaskId;

    @Test
    public void test() {
        this.longTermTaskCheckService.checkIsExistLongTermTaskById(this.longTermtaskId);
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
