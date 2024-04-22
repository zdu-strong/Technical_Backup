package com.springboot.project.format;

import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.model.UserMessageModel;

@Service
public class UserMessageFormatter extends BaseService {

    private UserMessageModel format(UserMessageEntity userMessageEntity) {
        var userMessageModel = new UserMessageModel();
        BeanUtils.copyProperties(userMessageEntity, userMessageModel);
        userMessageModel.setUser(this.userFormatter.format(userMessageEntity.getUser()));
        return userMessageModel;
    }

    @SuppressWarnings("resource")
    public UserMessageModel formatForUserId(UserMessageEntity userMessageEntity, String userId) {
        var userMessage = this.format(userMessageEntity);
        var crateDate = userMessage.getCreateDate();
        var id = userMessage.getId();
        var pageNum = this.UserMessageEntity()
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageRelevanceList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null || !s.getTwo().getIsDeleted())
                .select(s -> s.getOne())
                .where(s -> crateDate.after(s.getCreateDate())
                        || (crateDate.equals(s.getCreateDate())
                                && JPQLFunction.isSortAtBefore(s.getId(), id)))
                .count();
        userMessage.setPageNum(pageNum + 1);
        var isDeleted = !this.UserMessageEntity()
                .where(s -> s.getId().equals(id))
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageRelevanceList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null || !s.getTwo().getIsDeleted())
                .exists();
        userMessage.setIsDeleted(isDeleted);

        if (!userMessage.getIsRecall() && !userMessage.getIsDeleted()
                && StringUtils.isNotBlank(userMessageEntity.getFolderName())) {
            userMessage
                    .setUrl(this.storage.getResoureUrlFromResourcePath(
                            Paths.get(userMessageEntity.getFolderName(), userMessageEntity.getFileName()).toString()));
        }
        if (userMessage.getIsRecall() || userMessage.getIsDeleted()
                || StringUtils.isNotBlank(userMessageEntity.getFolderName())) {
            userMessage.setContent("");
        }

        return userMessage;
    }
}
