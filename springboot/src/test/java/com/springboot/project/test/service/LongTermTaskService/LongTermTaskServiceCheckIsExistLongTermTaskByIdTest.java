package com.springboot.project.test.service.LongTermTaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceCheckIsExistLongTermTaskByIdTest extends BaseTest {

    private String longTermtaskId;

    @Test
    public void test() {
        this.longTermTaskService.checkIsExistLongTermTaskById(this.longTermtaskId);
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
