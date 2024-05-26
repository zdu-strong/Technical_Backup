package com.springboot.project.test.common.longtermtask;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.springboot.project.test.common.BaseTest.BaseTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.net.URISyntaxException;

public class LongTermTaskUtilTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var result = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            return ResponseEntity.ok().build();
        }), new ParameterizedTypeReference<Void>() {
        });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getBody());
    }

}
