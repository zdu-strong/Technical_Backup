package com.springboot.project.test.service.UserEmailService;

import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserEmailServiceCreateUserEmailTest extends BaseTest {
    private UserModel user;
    private String email;

    @Test
    public void test() throws URISyntaxException {
        this.userEmailService.createUserEmail(this.email, this.user.getId());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
        this.email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
    }

}
