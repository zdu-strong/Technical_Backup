package com.springboot.project.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserMessageService extends BaseService {

    public UserMessageModel sendMessage(UserMessageModel userMessageModel) {
        var userId = userMessageModel.getUser().getId();
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).getOnlyValue();
        var userMessageEntity = new UserMessageEntity();
        userMessageEntity.setId(newId());
        userMessageEntity.setCreateDate(new Date());
        userMessageEntity.setUpdateDate(new Date());
        userMessageEntity.setContent(userMessageModel.getContent());
        userMessageEntity.setIsRecall(false);
        userMessageEntity.setUser(userEntity);
        userMessageEntity.setFolderName("");
        userMessageEntity.setFileName("");
        userMessageEntity.setFolderSize(0L);

        if (StringUtils.isNotBlank(userMessageModel.getUrl())) {
            var storageFileModel = this.storage.storageUrl(userMessageModel.getUrl());
            userMessageEntity.setFolderName(storageFileModel.getFolderName())
                    .setFolderSize(storageFileModel.getFolderSize())
                    .setFileName(storageFileModel.getFileName())
                    .setContent("");
        }
        this.persist(userMessageEntity);

        return this.userMessageFormatter.formatForUserId(userMessageEntity, userId);
    }

    public void recallMessage(String id) {
        var userMessageEntity = this.UserMessageEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        userMessageEntity.setIsRecall(true);
        userMessageEntity.setUpdateDate(new Date());
        this.merge(userMessageEntity);
    }

    public void deleteMessage(String id, HttpServletRequest request) {
        var userId = this.permissionUtil.getUserId(request);
        var userMessageId = id;
        this.userMessageRelevanceService.create(userMessageId, userId, true);
    }

    public UserMessageModel getUserMessageById(String id, String userId) {
        var userMessageEntity = this.UserMessageEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        return this.userMessageFormatter.formatForUserId(userMessageEntity, userId);
    }

    public List<UserMessageModel> getMessageListOnlyContainsOneByPageNum(Long pageNum, String userId) {
        var stream = this.UserMessageEntity()
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageRelevanceList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null || !s.getTwo().getIsDeleted())
                .select(s -> s.getOne())
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        var userMessageList = new PaginationModel<>(pageNum, 1L, stream,
                (s) -> this.userMessageFormatter.formatForUserId(s, userId)).getList();
        return userMessageList;
    }

    public UserMessageWebSocketSendModel getMessageListByLastTwentyMessage(String userId) {
        var userMessageList = this.UserMessageEntity()
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageRelevanceList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null || !s.getTwo().getIsDeleted())
                .select(s -> s.getOne())
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate())
                .limit(20)
                .map(s -> this.userMessageFormatter.formatForUserId(s, userId)).toList();
        var totalPage = JinqStream.from(userMessageList).select(s -> s.getPageNum()).sortedDescendingBy(s -> s)
                .findFirst().orElse(0L);
        var userMessageWebSocketSendModel = new UserMessageWebSocketSendModel().setTotalPage(totalPage)
                .setList(userMessageList);
        return userMessageWebSocketSendModel;
    }
}
