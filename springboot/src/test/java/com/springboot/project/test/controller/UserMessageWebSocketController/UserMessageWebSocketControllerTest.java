package com.springboot.project.test.controller.UserMessageWebSocketController;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.utils.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

import io.reactivex.rxjava3.subjects.ReplaySubject;
import jakarta.websocket.CloseReason.CloseCodes;

public class UserMessageWebSocketControllerTest extends BaseTest {

    private String webSocketServer;
    private String accessToken;
    private UserModel user;
    private WebSocketClient webSocketClient;

    @Test
    public void test() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException,
            JsonProcessingException {
        URI url = new URIBuilder(webSocketServer).setPath("/user_message/websocket")
                .setParameter("accessToken", accessToken)
                .build();
        ReplaySubject<UserMessageWebSocketSendModel> subject = ReplaySubject.create();
        this.webSocketClient = new WebSocketClient(url) {

            @Override
            public void onOpen(ServerHandshake handshakeData) {

            }

            @Override
            public void onMessage(String message) {
                try {
                    subject.onNext(objectMapper.readValue(message, UserMessageWebSocketSendModel.class));
                } catch (JsonMappingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (code == CloseCodes.NORMAL_CLOSURE.getCode()) {
                    subject.onComplete();
                } else {
                    subject.onError(new RuntimeException(reason));
                }
            }

            @Override
            public void onError(Exception ex) {
                subject.onError(ex);
            }
        };
        this.webSocketClient.connectBlocking();
        var result = JinqStream.from(subject.take(1).toList().toFuture().get(10, TimeUnit.SECONDS)).getOnlyValue();
        var userMessage = JinqStream.from(result.getList()).getOnlyValue();
        assertEquals(1, result.getList().size());
        assertEquals(1, result.getTotalPage());
        assertEquals("Hello, World!", userMessage.getContent());
        assertNull(userMessage.getUrl());
        assertEquals(this.user.getId(), userMessage.getUser().getId());
        assertNotNull(userMessage.getCreateDate());
        assertNotNull(userMessage.getCreateDate());
        assertEquals(1, userMessage.getPageNum());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.webSocketServer = new URIBuilder("ws" + this.testRestTemplate.getRootUri().substring(4)).build()
                .toString();
        this.user = this.createAccount("zdu.strong@gmail.com");
        this.accessToken = this.user.getAccessToken();
        var userMessage = new UserMessageModel().setUser(this.user).setContent("Hello, World!");
        this.userMessageService.sendMessage(userMessage);
    }

    @AfterEach
    public void afterEach() throws InterruptedException {
        this.webSocketClient.closeBlocking();
    }
}
