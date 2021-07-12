package com.springboot.project.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.UserMessageModel;

@Service
public class UserMessageService extends BaseService {

    public UserMessageModel sendMessage(String userId, String content) {
        var gg = "";
        if("".equals(gg)){
            
        }
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).getOnlyValue();
        var userMessageEntity = new UserMessageEntity().setId(UUID.randomUUID().toString())
                .setCreateDate(new Date())
                .setUpdateDate(new Date()).setContent(content).setDeleteKey(null).setIsRecall(false)
                .setUser(userEntity);
        this.entityManager.persist(userEntity);
        return this.userMessageFormatter.formatForUserId(userMessageEntity, userId);
    }

    public void recallMessage(String id) {
        var userMessageEntity = this.UserMessageEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        userMessageEntity.setIsRecall(true);
        userMessageEntity.setUpdateDate(new Date());
        this.entityManager.merge(userMessageEntity);
    }

    public void deleteMessage(String id) {
        var userMessageEntity = this.UserMessageEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        userMessageEntity.setDeleteKey(UUID.randomUUID().toString());
        userMessageEntity.setUpdateDate(new Date());
        this.entityManager.merge(userMessageEntity);
    }

    public UserMessageModel getMessageByPageNum(Integer pageNum, String userId) {
        var stream = this.UserMessageEntity().sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        var pagination = new PaginationModel<>(pageNum, 1, stream,
                (s) -> this.userMessageFormatter.formatForUserId(s, userId));
        var userMessage = JinqStream.from(pagination.getList()).getOnlyValue();
        return userMessage;
    }

    public List<UserMessageModel> getMessageListByRecentUpdate(Date updateDate, String userId) {
        var userMessageList = this.UserMessageEntity().where(s -> s.getUpdateDate().after(updateDate))
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getUpdateDate())
                .map(s -> this.userMessageFormatter.formatForUserId(s, userId)).collect(Collectors.toList());
        return userMessageList;
    }

    public List<UserMessageModel> getLastMessageList(String userId) {
        var stream = this.UserMessageEntity().sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate());
        var pagination = new PaginationModel<>(1, 20, stream,
                (s) -> this.userMessageFormatter.formatForUserId(s, userId));
        var messageList = pagination.getList();
        Collections.reverse(messageList);
        return messageList;
    }
}
