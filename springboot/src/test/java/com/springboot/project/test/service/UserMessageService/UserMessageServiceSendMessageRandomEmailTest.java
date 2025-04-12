package com.springboot.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceSendMessageRandomEmailTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() {
        var message = this.userMessageService.sendMessage(userMessage);
        assertEquals(36, message.getId().length());
        assertEquals("Hello, World!", message.getContent());
        assertEquals(this.userMessage.getUser().getId(), message.getUser().getId());
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this.createAccount(Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com")
                .getId();
        this.userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
    }
}
