package com.john.project.test.common.FieldValidationUtil;

import com.john.project.test.common.BaseTest.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ValidationFieldUtilTest extends BaseTest {

    private String id;

    @Test
    public void test() {
        var exceptException = Assertions.assertThrowsExactly(ResponseStatusException.class, () -> {
            validationFieldUtil.checkNotBlankOfId(id);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exceptException.getStatusCode());
        assertEquals("id cannot be blank", exceptException.getReason());
    }

    @BeforeEach
    public void beforeEach() {
        this.id = StringUtils.SPACE;
    }

}
