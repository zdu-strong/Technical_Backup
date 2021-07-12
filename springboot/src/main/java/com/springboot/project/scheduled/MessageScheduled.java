package com.springboot.project.scheduled;

import org.jinq.orm.stream.JinqStream;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.controller.UserMessageWebSocketController;

@Component
@EnableScheduling
public class MessageScheduled {
	@Scheduled(initialDelay = 1000, fixedDelay = 500)
	public void scheduled() {
		for (var group : JinqStream.from(UserMessageWebSocketController.getWebSocketList())
				.group((s) -> s.getUserId(), (m, n) -> n.toList()).toList()) {
			for (var webSocket : group.getTwo()) {
				webSocket.sendMessage();
			}
		}
	}

}
