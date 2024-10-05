package com.springboot.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskByErrorMessageTest extends BaseTest {
    private String longTermtaskId;

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        this.longTermTaskService.updateLongTermTaskByErrorMessage(this.longTermtaskId,
                new RuntimeException("Internal Server Error"));
        var result = (ResponseEntity<JsonNode>) this.longTermTaskService.getLongTermTask(this.longTermtaskId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Internal Server Error", result.getBody().get("message").asText());
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
