package com.springboot.project.websocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketReceiveModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;
import com.springboot.project.service.UserMessageService;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;

/**
 * Required parameters: String access_token;
 * 
 * @author zdu
 *
 */
@ServerEndpoint("/user_message/websocket")
@Component
public class UserMessageWebSocket {

    /**
     * Public accessible properties
     */
    @Getter
    private final static CopyOnWriteArrayList<UserMessageWebSocket> staticWebSocketList = new CopyOnWriteArrayList<UserMessageWebSocket>();

    /**
     * Public accessible properties
     */
    @Getter
    private String userId;
    private String accessToken;
    private Session session;
    private UserMessageWebSocketSendModel lastMessage = new UserMessageWebSocketSendModel().setTotalPage(0L)
            .setList(Lists.newArrayList());
    private ConcurrentMap<Long, UserMessageModel> onlineMessageMap = new ConcurrentHashMap<>();
    private boolean ready = false;

    public synchronized void sendMessage() throws IOException {
        try {
            getPermissionUtil().checkIsSignIn(accessToken);
            this.sendMessageForLastMessage();
            this.sendMessageForOnlineMessage();
        } catch (IllegalStateException | ResponseStatusException e) {
            this.OnError(session, e);
        } catch (Throwable e) {
            this.OnError(session, e);
            throw e;
        }
    }

    private void sendMessageForLastMessage() throws IOException {
        var lastTwentyMessage = this.getUserMessageService().getMessageListByLastTwentyMessage(userId);
        var hasChange = !this.getObjectMapper().writeValueAsString(lastTwentyMessage).equals(
                this.getObjectMapper().writeValueAsString(this.lastMessage));
        if (!this.ready || hasChange) {
            var newUserMessageWebSocketSendModel = new UserMessageWebSocketSendModel()
                    .setTotalPage(lastTwentyMessage.getTotalPage());
            var list = JinqStream.from(lastTwentyMessage.getList())
                    .where(s -> !JinqStream.from(this.lastMessage.getList()).anyMatch(t -> {
                        try {
                            return this.getObjectMapper().writeValueAsString(s)
                                    .equals(this.getObjectMapper().writeValueAsString(t));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }))
                    .toList();
            newUserMessageWebSocketSendModel.setList(list);
            this.session.getBasicRemote()
                    .sendText(this.getObjectMapper()
                            .writeValueAsString(newUserMessageWebSocketSendModel));
            this.lastMessage = lastTwentyMessage;
            this.ready = true;
        }
    }

    private void sendMessageForOnlineMessage() throws IOException {
        for (var pageNum : this.onlineMessageMap.keySet()) {
            if (pageNum > this.lastMessage.getTotalPage() - 20) {
                continue;
            }

            if (this.lastMessage.getList().stream().anyMatch(s -> s.getPageNum() == (long) pageNum)) {
                continue;
            }

            var userMessageList = this.getUserMessageService().getMessageListOnlyContainsOneByPageNum(pageNum,
                    this.userId);
            if (userMessageList.isEmpty()) {
                continue;
            }

            var oldUserMessage = this.onlineMessageMap.getOrDefault(pageNum, new UserMessageModel());
            var userMessage = JinqStream.from(userMessageList).getOnlyValue();
            var hasChange = !this.getObjectMapper().writeValueAsString(oldUserMessage)
                    .equals(this.getObjectMapper().writeValueAsString(userMessage));
            if (!hasChange) {
                continue;
            }

            if (!this.onlineMessageMap.containsKey(pageNum)) {
                continue;
            }
            this.session.getBasicRemote()
                    .sendText(this.getObjectMapper().writeValueAsString(new UserMessageWebSocketSendModel()
                            .setList(List.of(userMessage)).setTotalPage(null)));
            this.onlineMessageMap.replace(pageNum, userMessage);
        }
    }

    /**
     * @param session
     * @param email
     * @throws InterruptedException
     */
    @OnOpen
    public void onOpen(Session session) throws URISyntaxException, IOException, InterruptedException {
        /**
         * Get access token
         */
        var accessToken = JinqStream.from(new URIBuilder(session.getRequestURI()).getQueryParams())
                .where(s -> s.getName().equals("accessToken"))
                .select(s -> s.getValue())
                .findOne()
                .orElse("");
        this.getPermissionUtil().checkIsSignIn(accessToken);
        var userId = this.getPermissionUtil().getUserId(accessToken);
        /**
         * Save properties to member variables
         */
        this.userId = userId;
        this.session = session;
        this.accessToken = accessToken;
        staticWebSocketList.add(this);
    }

    @OnClose
    public void onClose() {
        staticWebSocketList.remove(this);
    }

    @OnError
    public void OnError(Session session, Throwable e) throws IOException {
        session
                .close(new CloseReason(CloseCodes.CLOSED_ABNORMALLY, e.getMessage()));
    }

    /**
     * @param userMessageWebSocketReceiveModelString UserMessageWebSocketReceiveModel
     * @param session
     */
    @OnMessage
    public void OnMessage(String userMessageWebSocketReceiveModelString)
            throws IOException, InterruptedException {
        var userMessageWebSocketReceiveModel = this.getObjectMapper().readValue(userMessageWebSocketReceiveModelString,
                UserMessageWebSocketReceiveModel.class);
        if (userMessageWebSocketReceiveModel.getIsCancel()) {
            this.onlineMessageMap.remove(userMessageWebSocketReceiveModel.getPageNum());
        } else {
            this.onlineMessageMap.put(userMessageWebSocketReceiveModel.getPageNum(), new UserMessageModel());
        }
    }

    private UserMessageService getUserMessageService() {
        return SpringUtil.getBean(UserMessageService.class);
    }

    private PermissionUtil getPermissionUtil() {
        return SpringUtil.getBean(PermissionUtil.class);
    }

    private ObjectMapper getObjectMapper() {
        return SpringUtil.getBean(ObjectMapper.class);
    }
}
