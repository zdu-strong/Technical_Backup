package com.springboot.project.test.controller.UserMessageController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserMessageControllerSendMessageTest extends BaseTest {

    private String userId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/user_message/send").build();
        var body = new UserMessageModel();
        body.setUser(new UserModel().setId(userId));
        body.setContent("Hello, World!");
        var response = this.testRestTemplate.postForEntity(url, body, UserMessageModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals(36, response.getBody().getId().length());
        assertEquals("Hello, World!", response.getBody().getContent());
        assertTrue(StringUtils.isNotBlank(response.getBody().getUser().getId()));
        assertEquals("zdu.strong@gmail.com", response.getBody().getUser().getUsername());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.userId = this.createAccount("zdu.strong@gmail.com").getId();
    }

}
