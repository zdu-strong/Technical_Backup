package com.springboot.project.test.service.UserCheckService;

import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserServiceCheckExistAccountTest extends BaseTest {
    private String email;

    @Test
    public void test() throws URISyntaxException {
        this.userCheckService.checkExistAccount(this.email);
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(email);
    }

}
