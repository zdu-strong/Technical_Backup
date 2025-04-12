package com.john.project.test.controller.UserMessageController;

import static org.junit.jupiter.api.Assertions.*;

import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.john.project.model.UserMessageModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageControllerDeleteMessageTest extends BaseTest {
    private String id;

    @Test
    @SneakyThrows
    public void test() {
        var url = new URIBuilder("/user-message/delete").setParameter("id", id).build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        this.createAccount(email);
        var userMessage = new UserMessageModel().setContent("Hello, World!");
        this.id = this.userMessageService.sendMessage(userMessage, request).getId();
    }

}
