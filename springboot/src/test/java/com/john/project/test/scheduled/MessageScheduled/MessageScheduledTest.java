package com.john.project.test.scheduled.MessageScheduled;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import com.fasterxml.uuid.Generators;
import com.john.project.model.UserMessageModel;
import com.john.project.model.UserMessageWebSocketSendModel;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;
import static eu.ciechanowiec.sneakyfun.SneakyFunction.sneaky;

public class MessageScheduledTest extends BaseTest {

    private UserModel user;

    private UserMessageModel userMessage;

    @Test
    public void test() {
        assertEquals("Hello, World!", userMessage.getContent());
        assertNull(userMessage.getUrl());
        assertEquals(this.user.getId(), userMessage.getUser().getId());
        assertNotNull(userMessage.getCreateDate());
        assertNotNull(userMessage.getCreateDate());
        assertEquals(1, userMessage.getPageNum());
    }

    @BeforeEach
    @SneakyThrows
    public void beforeEach() {
        {
            var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
            this.user = this.createAccount(email);
            var userMessage = new UserMessageModel().setUser(this.user).setContent("Hello, World!");
            this.userMessageService.sendMessage(userMessage);
        }
        {
            URI url = new URIBuilder(this.serverAddressProperties.getWebSocketServerAddress())
                    .setPath("/web-socket/user-message")
                    .setParameter("accessToken", this.user.getAccessToken())
                    .build();
            var userMessageResultList = new ArrayList<UserMessageWebSocketSendModel>();
            new StandardWebSocketClient().execute(url, (session) -> session
                    .receive()
                    .map(sneaky((s) -> {
                        userMessageResultList
                                .add(this.objectMapper.readValue(s.getPayloadAsText(),
                                        UserMessageWebSocketSendModel.class));
                        return session.close();
                    }))
                    .then())
                    .block(Duration.ofMinutes(1));
            assertEquals(1, userMessageResultList.size());
            assertEquals(1, JinqStream.from(userMessageResultList).select(s -> s.getTotalPages()).getOnlyValue());
            this.userMessage = JinqStream.from(userMessageResultList).selectAllList(s -> s.getItems()).getOnlyValue();
        }
    }

}
