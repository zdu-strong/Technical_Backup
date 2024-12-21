package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class LongTermTaskControllerGetLongTermTaskIncorrectFormatIdTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/long_term_task")
                .setParameter("encryptedId", Generators.timeBasedReorderedGenerator().generate().toString())
                .build();
        var result = this.testRestTemplate.getForEntity(url, Throwable.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Illegal base64 character 2d", result.getBody().getMessage());
    }

}
