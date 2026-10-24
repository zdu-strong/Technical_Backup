package com.john.project.test.controller.UserController;

import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerUpdateTest extends BaseTest {

    private UserModel user;
    private String email;

    @Test
    @SneakyThrows
    public void test() {
        {
            var url = new URIBuilder("/user/update/ml-kem/one-time").build();
            var response = this.testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(user), Void.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
        {
            var url = new URIBuilder("/user").setParameter("id", this.user.getId()).build();
            var response = this.testRestTemplate.getForEntity(url, UserModel.class);
            assertEquals(this.user.getId(), response.getBody().getId());
            assertTrue(StringUtils.isNotBlank(response.getBody().getUsername()));
            assertTrue(StringUtils.isBlank(response.getBody().getPassword()));
            assertTrue(StringUtils.isBlank(response.getBody().getAccessToken()));
            assertNotNull(response.getBody().getCreateDate());
            assertNotNull(response.getBody().getUpdateDate());
            assertEquals(0, response.getBody().getUserEmailList().size());
        }
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        this.email = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        this.user = this.createAccountOfSuperAdmin(this.email);
    }

}
