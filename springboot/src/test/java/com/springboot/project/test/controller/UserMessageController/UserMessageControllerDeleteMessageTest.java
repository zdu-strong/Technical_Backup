package com.springboot.project.test.controller.UserMessageController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;

import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserMessageControllerDeleteMessageTest extends BaseTest {
    private String id;
    private String userId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/user_message/delete").setParameter("id", id).build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.userId = this.createAccount("zdu.strong@gmail.com").getId();
        var userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
        this.id = this.userMessageService.sendMessage(userMessage).getId();
    }

}
