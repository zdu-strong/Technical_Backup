package com.john.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.john.project.model.UserMessageModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceSendMessageRandomEmailTest extends BaseTest {
    private UserMessageModel userMessage;
    private String userId;

    @Test
    public void test() {
        var message = this.userMessageService.sendMessage(userMessage, request);
        assertEquals(36, message.getId().length());
        assertEquals("Hello, World!", message.getContent());
        assertEquals(userId, message.getUser().getId());
    }

    @BeforeEach
    public void beforeEach() {
        this.userId = this.createAccount(Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com").getId();
        this.userMessage = new UserMessageModel().setContent("Hello, World!");
    }
}
