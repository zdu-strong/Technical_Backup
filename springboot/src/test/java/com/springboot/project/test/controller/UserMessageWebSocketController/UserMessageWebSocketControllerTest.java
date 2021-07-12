package com.springboot.project.test.controller.UserMessageWebSocketController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.client.utils.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.alibaba.fastjson.JSON;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.test.BaseTest;

import io.reactivex.subjects.ReplaySubject;

public class UserMessageWebSocketControllerTest extends BaseTest {

    private String webSocketServer;

    @Test
    public void test() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
        URI url = new URIBuilder(webSocketServer).setPath("/message").setParameter("email", "zdu.strong@gmail.com")
                .build();
        ReplaySubject<List<UserMessageModel>> subject = ReplaySubject.create();
        WebSocketClient webSocketClient = new WebSocketClient(url) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {

            }

            @Override
            public void onMessage(String message) {
                subject.onNext(JSON.parseArray(message, UserMessageModel.class));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (code == 0) {
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
        webSocketClient.connectBlocking();
        var result = subject.take(1).toList().toFuture().get(5, TimeUnit.SECONDS);
        webSocketClient.closeBlocking();
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(JinqStream.from(result).getOnlyValue().size() > 0);
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.webSocketServer = new URIBuilder("ws" + this.testRestTemplate.getRootUri().substring(4)).build()
                .toString();
        Mockito.reset(this.permissionUtil);
        var user = this.userService.createUserIfNotExist("zdu.strong@gmail.com");
        this.userMessageService.sendMessage(user.getId(), "Hello, World!");
    }
}
