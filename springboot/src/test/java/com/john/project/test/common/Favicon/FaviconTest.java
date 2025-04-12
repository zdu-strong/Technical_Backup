package com.john.project.test.common.Favicon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.john.project.test.common.BaseTest.BaseTest;

public class FaviconTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        URI url = new URIBuilder("/favicon.ico").build();
        var response = this.testRestTemplate.getForEntity(url, byte[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(85886, response.getBody().length);
    }

}
