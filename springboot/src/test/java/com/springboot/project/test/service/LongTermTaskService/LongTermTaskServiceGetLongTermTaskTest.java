package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceGetLongTermTaskTest extends BaseTest {

    private String longTermtaskId;

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        var result = (ResponseEntity<LongTermTaskModel<?>>) this.longTermTaskService
                .getLongTermTask(this.longTermtaskId);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody().getId());
        assertFalse(result.getBody().getIsDone());
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
