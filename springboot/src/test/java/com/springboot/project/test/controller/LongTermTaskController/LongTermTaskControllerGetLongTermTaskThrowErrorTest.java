package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskControllerGetLongTermTaskThrowErrorTest extends BaseTest {

    @Test
    public void test() {
        assertThrows(HttpClientErrorException.class, () -> {
            this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed due to insufficient funds");
            }), new ParameterizedTypeReference<String>() {
            });
        });
    }

}
