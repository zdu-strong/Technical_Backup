package com.springboot.project.scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

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

    private void sendMessage() throws Throwable {
        var websocketList = JinqStream.from(UserMessageWebSocketController.getStaticWebSocketList())
                .sortedBy(s -> s.getUserId()).toList();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var semaphore = new Semaphore(2);
            var futureList = new ArrayList<Future<?>>();
            for (var websocket : websocketList) {
                futureList.add(executor.submit(() -> {
                    try {
                        semaphore.acquire();
                        websocket.sendMessage();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    } finally {
                        semaphore.release();
                    }
                }));
            }
            for (var future : futureList) {
                future.get();
            }
        }
    }

}
