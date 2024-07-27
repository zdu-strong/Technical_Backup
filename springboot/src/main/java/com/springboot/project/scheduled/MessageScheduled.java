package com.springboot.project.scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jinq.orm.stream.JinqStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.project.websocket.UserMessageWebSocket;

@Component
public class MessageScheduled {

    @Scheduled(initialDelay = 1000, fixedDelay = 1)
    public void scheduled() throws Throwable {
        sendMessage();
    }

    private void sendMessage() throws Throwable {
        var websocketList = JinqStream.from(UserMessageWebSocket.getStaticWebSocketList())
                .sortedBy(s -> s.getUserId()).toList();
        if (websocketList.isEmpty()) {
            return;
        }
        try (var executor = Executors.newSingleThreadExecutor()) {
            var futureList = new ArrayList<Future<?>>();
            for (var websocket : websocketList) {
                futureList.add(executor.submit(() -> {
                    try {
                        websocket.sendMessage();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }));
            }
            for (var future : futureList) {
                future.get();
            }
        }
    }

}
