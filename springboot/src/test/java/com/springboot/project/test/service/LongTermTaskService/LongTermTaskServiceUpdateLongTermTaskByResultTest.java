package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskByResultTest extends BaseTest {
    private String longTermtaskId;

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        this.longTermTaskService.updateLongTermTaskByResult(this.longTermtaskId, ResponseEntity.ok("Hello, World!"));
        var result = (ResponseEntity<LongTermTaskModel<String>>) this.longTermTaskService
                .getLongTermTask(this.longTermtaskId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Hello, World!", result.getBody().getResult());
        assertTrue(result.getBody().getIsDone());
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
