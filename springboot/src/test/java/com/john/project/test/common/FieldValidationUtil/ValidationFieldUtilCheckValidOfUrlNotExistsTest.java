package com.john.project.test.common.FieldValidationUtil;

import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationFieldUtilCheckValidOfUrlNotExistsTest extends BaseTest {

    private String url;

    @Test
    public void test() {
        var exceptException = Assertions.assertThrowsExactly(ResponseStatusException.class, () -> {
            this.validationFieldUtil.checkValidOfUrl(url);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exceptException.getStatusCode());
        assertEquals("The file is invalid", exceptException.getReason());

    }

    @BeforeEach
    public void beforeEach() {
        this.url = this.storage.getResoureUrlFromResourcePath(Generators.timeBasedReorderedGenerator().generate().toString());
    }

}
