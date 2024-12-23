package com.springboot.project.test.scheduled.MessageScheduled;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.hc.core5.net.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;
import static eu.ciechanowiec.sneakyfun.SneakyFunction.sneaky;

public class MessageScheduledTest extends BaseTest {

    private UserModel user;

    private UserMessageModel userMessage;

    @Test
    public void test() throws Throwable {
        assertEquals("Hello, World!", userMessage.getContent());
        assertNull(userMessage.getUrl());
        assertEquals(this.user.getId(), userMessage.getUser().getId());
        assertNotNull(userMessage.getCreateDate());
        assertNotNull(userMessage.getCreateDate());
        assertEquals(1, userMessage.getPageNum());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException,
            JsonProcessingException {
        {
            var email = Generators.timeBasedReorderedGenerator().generate().toString() + "@gmail.com";
            this.user = this.createAccount(email);
            var userMessage = new UserMessageModel().setUser(this.user).setContent("Hello, World!");
            this.userMessageService.sendMessage(userMessage);
        }
        {
            URI url = new URIBuilder(this.serverAddressProperties.getWebSocketServerAddress())
                    .setPath("/web_socket/user_message")
                    .setParameter("accessToken", this.user.getAccessToken())
                    .build();
            var userMessageResultList = new ArrayList<UserMessageWebSocketSendModel>();
            var webSocketClient = new StandardWebSocketClient();
            webSocketClient.execute(url, (session) -> session
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
            assertEquals(1, JinqStream.from(userMessageResultList).select(s -> s.getTotalPage()).getOnlyValue());
            this.userMessage = JinqStream.from(userMessageResultList).selectAllList(s -> s.getList()).getOnlyValue();
        }
    }

}
