package com.springboot.project.scheduled;

import org.jinq.orm.stream.JinqStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.project.controller.UserMessageWebSocketController;

@Component
public class MessageScheduled {

    @Scheduled(initialDelay = 1000, fixedDelay = 1)
    public void scheduled() throws Throwable {
        sendMessage();
    }

    public void sendMessage() throws Throwable {
        var websocketList = JinqStream.from(UserMessageWebSocketController.getStaticWebSocketList())
                .sortedBy(s -> s.getUserId()).toList();
        Throwable error = null;
        for (var websocket : websocketList) {
            try {
                websocket.sendMessage();
            } catch (Throwable e) {
                error = e;
            }
        }
        if (error != null) {
            throw error;
        }
    }

}
