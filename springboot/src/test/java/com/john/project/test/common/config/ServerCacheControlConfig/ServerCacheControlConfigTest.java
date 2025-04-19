package com.john.project.test.common.config.ServerCacheControlConfig;

import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ServerCacheControlConfigTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder("/").build();
        ResponseEntity<String> response = this.testRestTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(13, response.getBody().length());
        assertEquals("Hello, World!", response.getBody());
        assertEquals("no-store", response.getHeaders().getCacheControl());
    }

}
