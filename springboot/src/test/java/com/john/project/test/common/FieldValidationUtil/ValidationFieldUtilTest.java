package com.john.project.test.common.FieldValidationUtil;

import com.john.project.test.common.BaseTest.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;


public class ValidationFieldUtilTest extends BaseTest {

    private String id;

    @Test
    public void test() {
        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> {
            validationFieldUtil.checkNotBlankOfId(id);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.id = StringUtils.SPACE;
    }

}
