package com.john.project.test.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserServiceCheckExistAccountTest extends BaseTest {
    private String email;

    @Test
    public void test() {
        this.userService.checkExistAccount(this.email);
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(email);
    }

}
