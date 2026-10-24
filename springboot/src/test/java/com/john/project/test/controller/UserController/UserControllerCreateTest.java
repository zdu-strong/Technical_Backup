package com.john.project.test.controller.UserController;

import com.google.common.collect.Lists;
import com.john.project.model.UserEmailModel;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerCreateTest extends BaseTest {

    private UserModel user;
    private String email;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/user/create/ml-kem/one-time").build();
        var response = this.testRestTemplate.postForEntity(url, user, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
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
        this.createAccountOfSuperAdmin(this.uuidUtil.v4() + "zdu.strong@gmail.com");
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        var password = this.email;
        var verificationCodeEmail = sendVerificationCode(email);
        this.user = new UserModel()
                .setUsername(email)
                .setPassword(this.tokenService.getEncryptedPassword(password))
                .setUserEmailList(Lists.newArrayList(
                        new UserEmailModel()
                                .setEmail(email)
                                .setVerificationCodeEmail(verificationCodeEmail)))
                .setRoleList(List.of());
        ;
    }

}
