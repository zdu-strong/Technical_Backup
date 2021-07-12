package com.springboot.project.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.service.UserMessageService;
import com.springboot.project.service.UserService;
import lombok.Getter;

/**
 * Required parameters: String access_token;
 * 
 * @author zdu
 *
 */
@ServerEndpoint("/message")
@Component
public class UserMessageWebSocketController {

    /**
     * Autowired
     */
    private static UserService userService;

    /**
     * Autowired
     */
    private static UserMessageService userMessageService;

    /**
     * Public accessible properties
     */
    @Getter
    private final static CopyOnWriteArrayList<UserMessageWebSocketController> webSocketList = new CopyOnWriteArrayList<UserMessageWebSocketController>();

    /**
     * Public accessible properties
     */
    @Getter
    private String userId;
    private Session session;
    private List<UserMessageModel> lastMessage = Lists.newArrayList();

    @OnOpen
    public void onOpen(Session session) throws URISyntaxException, IOException {
        /**
         * Get access token
         */
        var emailList = JinqStream.from(new URIBuilder(session.getRequestURI()).getQueryParams())
                .where(s -> s.getName().equals("email")).select(s -> s.getValue()).toList();
        if (emailList.isEmpty() || emailList.size() > 1) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "无效的邮箱地址");
        }
        var email = JinqStream.from(emailList).getOnlyValue();
        var userId = userService.createUserIfNotExist(email).getId();
        /**
         * Save properties to member variables
         */
        this.userId = userId;
        this.session = session;
        var messageList = userMessageService.getLastMessageList(userId);
        this.session.getBasicRemote().sendText(JSON.toJSONString(messageList));
        lastMessage = messageList;
        webSocketList.add(this);
    }

    @OnMessage
    public void OnMessage(Session session, Integer pageNum) throws IOException {
        var userMessage = userMessageService.getMessageByPageNum(pageNum, userId);
        var messageList = Lists.newArrayList(userMessage);
        this.session.getBasicRemote().sendText(JSON.toJSONString(messageList));
    }

    @OnClose
    public void onClose() {
        webSocketList.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        session.close(new CloseReason(CloseCodes.TRY_AGAIN_LATER, error.getMessage()));
    }

    @Autowired
    public void setUserService(UserService _userService) {
        userService = _userService;
    }

    @Autowired
    public void setUserMessageService(UserMessageService _userMessageService) {
        userMessageService = _userMessageService;
    }

    /**
     * Get the message content of the current user this time
     * 
     * @return
     */
    private List<UserMessageModel> getMessage() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -2);
        var now = calendar.getTime();
        return userMessageService.getMessageListByRecentUpdate(now, userId);
    }

    public void sendMessage() {
        try {
            var messageList = this.getMessage();
            var newMessageList = messageList.stream().filter(
                    s -> !lastMessage.stream().anyMatch(t -> {
                        var objectOne = new UserMessageModel();
                        var objectTwo = new UserMessageModel();
                        BeanUtils.copyProperties(s, objectOne, "totalPage");
                        BeanUtils.copyProperties(t, objectTwo, "totalPage");
                        return JSON.toJSONString(objectOne).equals(JSON.toJSONString(objectTwo));
                    }))
                    .collect(Collectors.toList());
            if (!newMessageList.isEmpty()) {
                this.session.getBasicRemote().sendText(JSON.toJSONString(newMessageList));
                lastMessage = messageList;
            }
        } catch (Throwable e) {
            try {
                this.session.close(new CloseReason(CloseCodes.TRY_AGAIN_LATER, e.getMessage()));
            } catch (IOException e1) {
                throw new RuntimeException(e1.getMessage(), e1);
            }
        }
    }

}
