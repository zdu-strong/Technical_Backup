package com.john.project.test.common.FieldValidationUtil;

import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class ValidationFieldUtilCheckValidOfUrlTest extends BaseTest {

    private String url;

    @Test
    public void test() {
        this.validationFieldUtil.checkValidOfUrl(url);
    }

    @BeforeEach
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.url = storageFileModel.getRelativeUrl();
    }

}
