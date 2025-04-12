package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.google.common.collect.Lists;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskControllerGetLongTermTaskReturnArrayTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var result = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            var httpHeaders = new HttpHeaders();
            httpHeaders.addAll("MyCustomHeader", Lists.newArrayList("Hello, World!"));
            httpHeaders.set("MySecondCustomHeader", "Hello, World!");
            return ResponseEntity.ok().headers(httpHeaders).body(new String[] { "Hello, World!", "I love girl" });
        }), new ParameterizedTypeReference<String[]>() {
        });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().length);
        assertEquals("Hello, World!", result.getBody()[0]);
        assertEquals("I love girl", result.getBody()[1]);
        assertEquals(result.getHeaders().get("MyCustomHeader").size(), 1);
        assertEquals(result.getHeaders().get("MyCustomHeader").get(0), "Hello, World!");
        assertEquals(result.getHeaders().get("MySecondCustomHeader").get(0), "Hello, World!");
    }

}
