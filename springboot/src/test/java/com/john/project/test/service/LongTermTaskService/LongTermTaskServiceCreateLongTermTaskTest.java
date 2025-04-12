package com.john.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceCreateLongTermTaskTest extends BaseTest {

    @Test
    public void test() {
        var longTermtaskId = this.longTermTaskService.createLongTermTask();
        assertNotNull(longTermtaskId);
    }

}
