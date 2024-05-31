package com.springboot.project.test.common.Favicon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class FaviconTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        URI url = new URIBuilder("/favicon.ico").build();
        var response = this.testRestTemplate.getForEntity(url, byte[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3870, response.getBody().length);
    }

}
