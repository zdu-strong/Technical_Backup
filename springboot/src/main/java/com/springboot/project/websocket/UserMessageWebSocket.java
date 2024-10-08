package com.springboot.project.websocket;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.hibernate.exception.GenericJDBCException;
import org.jinq.orm.stream.JinqStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.google.common.collect.Lists;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketReceiveModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;
import com.springboot.project.service.UserMessageService;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Required parameters: String accessToken;
 * 
 * @author zdu
 *
 */
@ServerEndpoint("/user_message/websocket")
@Component
@Slf4j
public class UserMessageWebSocket {

    /**
     * Public accessible properties
     */
    @Getter
    private final static CopyOnWriteArrayList<UserMessageWebSocket> staticWebSocketList = new CopyOnWriteArrayList<UserMessageWebSocket>();
    private ObjectMapper objectMapper;
    private PermissionUtil permissionUtil;
    private UserMessageService userMessageService;
    private HttpServletRequest request;
    private UserMessageWebSocketSendModel lastMessage = new UserMessageWebSocketSendModel().setTotalPage(0L)
            .setList(Lists.newArrayList());
    private boolean ready = false;
    private ConcurrentHashMap<String, UserMessageModel> onlineMessageMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, UserMessageModel> onlineMessageReceiveDateMap = new ConcurrentHashMap<>();
    private Semaphore checkIsSignInSemaphore = new Semaphore(1);
    private Session webWosketSession;

    /**
     * @param session
     * @param email
     */
    @OnOpen
    public void onOpen(Session session) {
        this.objectMapper = SpringUtil.getBean(ObjectMapper.class);
        this.permissionUtil = SpringUtil.getBean(PermissionUtil.class);
        this.userMessageService = SpringUtil.getBean(UserMessageService.class);
        this.webWosketSession = session;
        this.request = this.getRequest(session);
        this.permissionUtil.checkIsSignIn(request);
        staticWebSocketList.add(this);
    }

    /**
     * @param userMessageWebSocketReceiveModelString UserMessageWebSocketReceiveModel
     * @param session
     */
    @OnMessage
    public void OnMessage(String userMessageWebSocketReceiveModelString) throws IOException {
        Thread.startVirtualThread(() -> {
            try {
                var userMessageWebSocketReceiveModel = this.objectMapper.readValue(
                        userMessageWebSocketReceiveModelString,
                        UserMessageWebSocketReceiveModel.class);
                if (userMessageWebSocketReceiveModel.getIsCancel() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }
                if (userMessageWebSocketReceiveModel.getPageNum() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }
                if (userMessageWebSocketReceiveModel.getPageNum() < 1) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }

                var pageNum = userMessageWebSocketReceiveModel.getPageNum().toString();
                var onlineMessageReceiveDateModel = new UserMessageModel()
                        .setId(Generators.timeBasedReorderedGenerator().generate().toString())
                        .setPageNum(userMessageWebSocketReceiveModel.getPageNum())
                        .setCreateDate(new Date());
                if (!userMessageWebSocketReceiveModel.getIsCancel()) {
                    synchronized (this) {
                        this.onlineMessageMap.putIfAbsent(pageNum, new UserMessageModel());
                        this.onlineMessageReceiveDateMap.put(pageNum, onlineMessageReceiveDateModel);
                    }
                } else {
                    ThreadUtil.sleep(1000);
                    synchronized (this) {
                        var oldInfo = this.onlineMessageReceiveDateMap.getOrDefault(pageNum, null);
                        if (oldInfo != null) {
                            var canRemove = JinqStream.from(List.of(oldInfo, onlineMessageReceiveDateModel))
                                    .sortedBy(m -> m.getId())
                                    .sortedBy(m -> m.getCreateDate())
                                    .findFirst()
                                    .get()
                                    .getId().equals(oldInfo.getId());
                            if (!canRemove) {
                                return;
                            }
                        }
                        this.onlineMessageMap.remove(pageNum);
                        this.onlineMessageReceiveDateMap.remove(pageNum);
                    }
                }
            } catch (Throwable e) {
                this.OnError(this.webWosketSession, e);
            }
        });
    }

    @OnError
    public void OnError(Session session, Throwable e) {
        for (var i = 1; i > 0; i--) {
            if (StringUtils.isNotBlank(e.getMessage()) && e.getMessage()
                    .contains("An established connection was aborted by the software in your host machine")) {
                continue;
            } else if (e instanceof IllegalStateException) {
                continue;
            } else if (e instanceof GenericJDBCException) {
                continue;
            }
            log.error(e.getMessage(), e);
        }
        try {
            session.close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, e.getMessage()));
        } catch (IOException e1) {
            // do nothing
        }
    }

    @OnClose
    public void onClose() {
        staticWebSocketList.remove(this);
    }

    public void sendMessage() {
        try {
            checkIsSignIn();
            var pageSize = this.ready ? 1L : 20L;
            var userMessageWebSocketSendModel = this.userMessageService
                    .getMessageListByLastMessage(pageSize, request);
            if (this.ready && userMessageWebSocketSendModel.getTotalPage() < this.lastMessage.getTotalPage()) {
                this.sendMessageForAllOnlineMessage(userMessageWebSocketSendModel);
            } else if (!this.ready || (!this.objectMapper.writeValueAsString(this.lastMessage)
                    .equals(this.objectMapper.writeValueAsString(userMessageWebSocketSendModel)))) {
                this.sendAndUpdateOnlineMessage(userMessageWebSocketSendModel);
                this.ready = true;
            } else {
                this.sendMessageForOnlyOneOnlineMessage();
            }
        } catch (Throwable e) {
            this.OnError(webWosketSession, e);
        }
    }

    private void sendMessageForAllOnlineMessage(UserMessageWebSocketSendModel userMessageWebSocketSendModel)
            throws JsonProcessingException, IOException {
        var userMessageList = Flowable.fromIterable(this.onlineMessageMap.keySet())
                .filter(s -> !userMessageWebSocketSendModel.getList().stream()
                        .anyMatch(m -> s.equals(m.getPageNum().toString())))
                .map(s -> this.userMessageService
                        .getUserMessageByPagination(Long.parseLong(s), 1L, request)
                        .getList())
                .flatMap(s -> Flowable.fromIterable(s))
                .toList()
                .blockingGet();
        userMessageList.addAll(userMessageWebSocketSendModel.getList());
        this.sendAndUpdateOnlineMessage(
                new UserMessageWebSocketSendModel().setTotalPage(userMessageWebSocketSendModel.getTotalPage())
                        .setList(userMessageList));
    }

    private void sendMessageForOnlyOneOnlineMessage() throws IOException {
        if (RandomUtil.randomLong(2) == 0) {
            return;
        }

        var pageNum = getPageNumForOnlineMessage();
        if (StringUtils.isBlank(pageNum)) {
            return;
        }

        var userMessageList = this.userMessageService.getUserMessageByPagination(Long.parseLong(pageNum), 1L, request)
                .getList();

        this.sendAndUpdateOnlineMessage(new UserMessageWebSocketSendModel()
                .setList(userMessageList).setTotalPage(null));
    }

    private String getPageNumForOnlineMessage() {
        var pageNumList = this.onlineMessageMap.keySet().stream().toList();
        var pageNum = pageNumList
                .stream()
                .filter(s -> StringUtils.isBlank(this.onlineMessageMap.getOrDefault(s, new UserMessageModel()).getId()))
                .findFirst()
                .orElse(null);
        if (StringUtils.isBlank(pageNum) && !pageNumList.isEmpty()) {
            pageNum = pageNumList.get(RandomUtil.randomInt(pageNumList.size()));
        }
        return pageNum;
    }

    private MockHttpServletRequest getRequest(Session session) {
        var accessToken = JinqStream.from(new URIBuilder(session.getRequestURI())
                .getQueryParams())
                .where(s -> s.getName().equals("accessToken"))
                .select(s -> s.getValue())
                .findOne()
                .orElse("");
        var request = new MockHttpServletRequest();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        request.addHeader(HttpHeaders.AUTHORIZATION, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
        return request;
    }

    private void sendAndUpdateOnlineMessage(UserMessageWebSocketSendModel userMessageWebSocketSendModel)
            throws JsonProcessingException, IOException {
        var userMessageWebSocketSendNewModel = this.objectMapper.readValue(
                this.objectMapper.writeValueAsString(userMessageWebSocketSendModel),
                UserMessageWebSocketSendModel.class);
        for (var userMessage : this.lastMessage.getList()) {
            this.onlineMessageMap.replace(userMessage.getPageNum().toString(), userMessage);
        }
        userMessageWebSocketSendNewModel.setList(Flowable.fromIterable(userMessageWebSocketSendNewModel.getList())
                .filter(s -> hasChange(s))
                .filter(s -> userMessageWebSocketSendNewModel.getTotalPage() != null
                        || this.onlineMessageMap.getOrDefault(s.getPageNum().toString(), null) != null)
                .toList()
                .blockingGet());
        if (userMessageWebSocketSendNewModel.getTotalPage() == null
                && userMessageWebSocketSendNewModel.getList().isEmpty()) {
            return;
        }
        this.webWosketSession.getBasicRemote()
                .sendText(this.objectMapper
                        .writeValueAsString(userMessageWebSocketSendNewModel));
        for (var userMessage : userMessageWebSocketSendNewModel.getList()) {
            this.onlineMessageMap.replace(userMessage.getPageNum().toString(), userMessage);
        }
        if (userMessageWebSocketSendModel.getTotalPage() != null) {
            this.lastMessage = userMessageWebSocketSendModel;
        }
    }

    @SneakyThrows
    private boolean hasChange(UserMessageModel userMessage) {
        var pageNum = userMessage.getPageNum().toString();
        var oldUserMessage = this.onlineMessageMap.getOrDefault(pageNum, new UserMessageModel());
        var hasChange = !this.objectMapper.writeValueAsString(oldUserMessage)
                .equals(this.objectMapper.writeValueAsString(userMessage));
        return hasChange;
    }

    private void checkIsSignIn() throws Throwable {
        Thread.startVirtualThread(() -> {
            try {
                if (this.checkIsSignInSemaphore.tryAcquire()) {
                    try {
                        this.permissionUtil.checkIsSignIn(request);
                        ThreadUtil.sleep(1000);
                    } finally {
                        this.checkIsSignInSemaphore.release();
                    }
                }
            } catch (Throwable e) {
                this.OnError(webWosketSession, e);
            }
        });
    }

}
