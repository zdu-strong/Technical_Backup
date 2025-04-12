package com.john.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.john.project.model.UserMessageModel;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceSendMessageTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() {
        var result = this.userMessageService.sendMessage(userMessage);
        assertEquals(36, result.getId().length());
        assertEquals("Hello, World!", result.getContent());
        assertEquals(this.userMessage.getUser().getId(), result.getUser().getId());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
        var userId = this.createAccount(email).getId();
        this.userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
    }
}
