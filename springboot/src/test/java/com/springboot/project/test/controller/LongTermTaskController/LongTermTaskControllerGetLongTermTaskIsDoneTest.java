package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskControllerGetLongTermTaskIsDoneTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var result = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            return ResponseEntity.ok("Hello, World!");
        }), new ParameterizedTypeReference<String>() {
        });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Hello, World!", result.getBody());
    }

}
