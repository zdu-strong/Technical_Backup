package com.springboot.project.scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.websocket.UserMessageWebSocket;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageScheduled {

    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Scheduled(initialDelay = 0, fixedDelay = 10)
    public void scheduled() {
        sendMessage();
    }

    private void sendMessage() {
        Flowable.fromIterable(UserMessageWebSocket.getStaticWebSocketList())
                .concatMap(s -> Flowable.just(s)
                        .observeOn(Schedulers.from(executor))
                        .doOnNext(UserMessageWebSocket::sendMessage)
                        .onErrorComplete())
                .blockingSubscribe();
    }

}
