package com.john.project.test.controller.UserMessageController;

import static org.junit.jupiter.api.Assertions.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.john.project.model.UserMessageModel;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageControllerSendMessageTest extends BaseTest {

    private String userId;
    private String username;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/user-message/send").build();
        var body = new UserMessageModel();
        body.setUser(new UserModel().setId(userId));
        body.setContent("Hello, World!");
        var response = this.testRestTemplate.postForEntity(url, body, UserMessageModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals(36, response.getBody().getId().length());
        assertEquals("Hello, World!", response.getBody().getContent());
        assertTrue(StringUtils.isNotBlank(response.getBody().getUser().getId()));
        assertEquals(this.username, response.getBody().getUser().getUsername());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        this.username = email;
        this.userId = this.createAccount(email).getId();
    }

}
