package com.john.project.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.john.project.websocket.UserMessageWebSocket;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageScheduled {

    @Scheduled(initialDelay = 0, fixedDelay = 10)
    public void scheduled() {
        sendMessage();
    }

    private void sendMessage() {
        Flowable.fromIterable(UserMessageWebSocket.getStaticWebSocketList())
                .concatMap(s -> Flowable.just(s)
                        .doOnNext(UserMessageWebSocket::sendMessage)
                        .onErrorComplete())
                .blockingSubscribe();
    }

}
