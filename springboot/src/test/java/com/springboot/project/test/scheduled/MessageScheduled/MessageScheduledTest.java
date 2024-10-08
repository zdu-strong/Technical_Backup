package com.springboot.project.test.scheduled.MessageScheduled;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.hc.core5.net.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import jakarta.websocket.CloseReason.CloseCodes;
import lombok.SneakyThrows;

public class MessageScheduledTest extends BaseTest {

    private UserModel user;
    private ReplayProcessor<UserMessageWebSocketSendModel> replayProcessor;
    private WebSocketClient webSocketClient;

    @Test
    public void test() throws Throwable {
        var result = this.replayProcessor.take(1).toList().toFuture().get(10, TimeUnit.SECONDS);
        assertEquals(1, result.size());
        assertEquals(1, JinqStream.from(result).selectAllList(s -> s.getList()).count());
        assertEquals(1, JinqStream.from(result).select(s -> s.getTotalPage()).findFirst().get());
        assertEquals("Hello, World!", JinqStream.from(result).selectAllList(s -> s.getList())
                .where(s -> s.getUser().getId().equals(this.user.getId())).select(s -> s.getContent())
                .limit(1)
                .getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException,
            JsonProcessingException {
        var webSocketServer = new URIBuilder("ws" + this.testRestTemplate.getRootUri().substring(4)).build()
                .toString();
        this.user = this.createAccount("zdu.strong@gmail.com");
        var accessToken = this.user.getAccessToken();
        var userMessage = new UserMessageModel().setUser(this.user).setContent("Hello, World!");
        this.userMessageService.sendMessage(userMessage);
        URI url = new URIBuilder(webSocketServer).setPath("/user_message/websocket")
                .setParameter("accessToken", accessToken)
                .build();
        this.replayProcessor = ReplayProcessor.create(1);
        this.webSocketClient = new WebSocketClient(url) {

            @Override
            public void onOpen(ServerHandshake handshakeData) {

            }

            @Override
            @SneakyThrows
            public void onMessage(String message) {
                replayProcessor.onNext(objectMapper.readValue(message, UserMessageWebSocketSendModel.class));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (code == CloseCodes.NORMAL_CLOSURE.getCode()) {
                    replayProcessor.onComplete();
                } else {
                    replayProcessor.onError(new RuntimeException(reason));
                }
            }

            @Override
            public void onError(Exception ex) {
                replayProcessor.onError(ex);
            }
        };
        this.webSocketClient.connectBlocking();
    }

    @AfterEach
    public void afterEach() throws InterruptedException {
        this.webSocketClient.closeBlocking();
    }

}
