package com.john.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskControllerGetLongTermTaskTest extends BaseTest {

    @Test
    public void test() {
        var result = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            return ResponseEntity.ok("Hello, World!");
        }), new ParameterizedTypeReference<String>() {
        });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Hello, World!", result.getBody());
    }

}
