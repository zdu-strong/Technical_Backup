package com.springboot.project.test.service.UserEmailCheckService;

import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserEmailCheckServiceCheckEmailIsNotUsedTest extends BaseTest {
    private String email;

    @Test
    public void test() throws URISyntaxException {
        this.userEmailCheckService.checkEmailIsNotUsed(this.email);
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
    }

}
