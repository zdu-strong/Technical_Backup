package com.springboot.project.test.controller.UserController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserControllerGetUserInfoTest extends BaseTest {

    private UserModel user;
    private String email;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/get-user-info").build();
        var response = this.testRestTemplate.getForEntity(url, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.user.getId(), response.getBody().getId());
        assertTrue(StringUtils.isNotBlank(response.getBody().getUsername()));
        assertTrue(StringUtils.isBlank(response.getBody().getPassword()));
        assertTrue(StringUtils.isBlank(response.getBody().getAccessToken()));
        assertNotNull(response.getBody().getCreateDate());
        assertNotNull(response.getBody().getUpdateDate());
        assertEquals(1, response.getBody().getUserEmailList().size());
        assertEquals(this.email,
                JinqStream.from(response.getBody().getUserEmailList()).select(s -> s.getEmail()).getOnlyValue());
        assertTrue(StringUtils.isNotBlank(
                JinqStream.from(response.getBody().getUserEmailList()).select(s -> s.getId()).getOnlyValue()));
        assertNull(JinqStream.from(response.getBody().getUserEmailList())
                .select(s -> s.getVerificationCodeEmail()).getOnlyValue());
        assertTrue(StringUtils.isNotBlank(JinqStream.from(response.getBody().getUserEmailList())
                .select(s -> s.getUser().getId()).getOnlyValue()));
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        this.email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
    }

}
