package com.john.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;

public class LongTermTaskControllerGetLongTermTaskIncorrectFormatIdTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/long-term-task")
                .setParameter("encryptedId", Generators.timeBasedReorderedGenerator().generate().toString())
                .build();
        var result = this.testRestTemplate.getForEntity(url, Throwable.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Illegal base64 character 2d", result.getBody().getMessage());
    }

}
