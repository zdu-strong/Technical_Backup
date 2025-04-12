package com.john.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.john.project.model.UserMessageModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceRecallMessageTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() {
        this.userMessageService.recallMessage(this.userMessage.getId());
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this.createAccount(Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com")
                .getId();
        var userMessage = new UserMessageModel().setContent("Hello, World!");
        var message = this.userMessageService.sendMessage(userMessage, request);
        assertEquals(36, message.getId().length());
        assertEquals("Hello, World!", message.getContent());
        assertEquals(userId, message.getUser().getId());
        this.userMessage = message;
    }
}
