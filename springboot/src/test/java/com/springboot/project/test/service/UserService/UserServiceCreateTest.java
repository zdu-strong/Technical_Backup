package com.springboot.project.test.service.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserServiceCreateTest extends BaseTest {
    private String email;

    @Test
    public void test()
            throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException {
        var result = this.createAccount(this.email);
        assertTrue(StringUtils.isNotBlank(result.getId()));
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
    }

}
