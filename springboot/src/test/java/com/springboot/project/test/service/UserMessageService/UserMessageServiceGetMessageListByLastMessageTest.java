package com.springboot.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceGetMessageListByLastMessageTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var result = this.userMessageService.getMessageListByLastMessage(1L, request);
        assertEquals(1, result.getTotalPages());
        var message = JinqStream.from(result.getItems()).getOnlyValue();
        assertTrue(StringUtils.isNotBlank(message.getId()));
        assertTrue(StringUtils.isNotBlank(message.getContent()));
        assertNotNull(message.getCreateDate());
        assertTrue(message.getPageNum() >= 1);
        assertNotNull(message.getUpdateDate());
        assertNull(message.getUrl());
        assertTrue(StringUtils.isNotBlank(message.getUser().getId()));
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this
                .createAccount(Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com")
                .getId();
        var userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
        var message = this.userMessageService.sendMessage(userMessage);
        assertEquals(36, message.getId().length());
        assertEquals("Hello, World!", message.getContent());
        assertEquals(userId, message.getUser().getId());
    }
}
