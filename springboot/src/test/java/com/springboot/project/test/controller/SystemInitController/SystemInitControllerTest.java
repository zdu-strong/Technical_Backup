package com.springboot.project.test.controller.SystemInitController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class SystemInitControllerTest extends BaseTest {

    private String userId;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/system-init").setParameter("id", userId).build();
        var response = this.testRestTemplate.getForEntity(url, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

}
