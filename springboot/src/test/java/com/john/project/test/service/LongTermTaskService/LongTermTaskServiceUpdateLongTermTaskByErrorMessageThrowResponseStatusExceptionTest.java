package com.john.project.test.service.LongTermTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskServiceUpdateLongTermTaskByErrorMessageThrowResponseStatusExceptionTest extends BaseTest {
    private String longTermtaskId;

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        this.longTermTaskService.updateLongTermTaskByErrorMessage(this.longTermtaskId,
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request"));
        var result = (ResponseEntity<JsonNode>) this.longTermTaskService
                .getLongTermTask(this.longTermtaskId);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Bad Request", result.getBody().get("message").asText());
    }

    @BeforeEach
    public void BeforeEach() {
        this.longTermtaskId = this.longTermTaskService.createLongTermTask();
    }

}
