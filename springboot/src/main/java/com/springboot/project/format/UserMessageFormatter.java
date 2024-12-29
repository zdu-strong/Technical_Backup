package com.springboot.project.format;

import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.model.UserMessageModel;

@Service
public class UserMessageFormatter extends BaseService {

    public UserMessageModel formatForUserId(UserMessageEntity userMessageEntity, String userId) {
        var userMessage = this.format(userMessageEntity);
        userMessage.setPageNum(getPageNumOfUserMessageEntity(userMessageEntity, userId));
        var isActive = getIsActiveOfUserMessageEntity(userMessageEntity, userId);
        userMessage.setUrl(getUrlOfUserMessageEntity(userMessageEntity, isActive));
        userMessage.setContent(getContentOfUserMessageEntity(userMessageEntity, isActive));
        return userMessage;
    }

    private UserMessageModel format(UserMessageEntity userMessageEntity) {
        var userMessageModel = new UserMessageModel();
        BeanUtils.copyProperties(userMessageEntity, userMessageModel);
        userMessageModel.setUser(this.userFormatter.format(userMessageEntity.getUser()));
        return userMessageModel;
    }

    private String getContentOfUserMessageEntity(UserMessageEntity userMessageEntity, boolean isActive) {
        if (!isActive || userMessageEntity.getIsRecall()
                || StringUtils.isNotBlank(userMessageEntity.getFolderName())) {
            return "";
        } else {
            return userMessageEntity.getContent();
        }
    }

    private boolean getIsActiveOfUserMessageEntity(UserMessageEntity userMessageEntity, String userId) {
        var id = userMessageEntity.getId();
        var isActive = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageDeactivateList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null)
                .exists();
        return isActive;
    }

    private String getUrlOfUserMessageEntity(UserMessageEntity userMessageEntity, boolean isActive) {
        if (isActive && !userMessageEntity.getIsRecall()
                && StringUtils.isNotBlank(userMessageEntity.getFolderName())) {
            return this.storage.getResoureUrlFromResourcePath(
                    Paths.get(userMessageEntity.getFolderName(), userMessageEntity.getFileName()).toString());
        } else {
            return null;
        }
    }

    private Long getPageNumOfUserMessageEntity(UserMessageEntity userMessageEntity, String userId) {
        var crateDate = userMessageEntity.getCreateDate();
        var id = userMessageEntity.getId();
        var stream = this.streamAll(UserMessageEntity.class)
                .where(s -> !s.getIsRecall())
                .leftOuterJoin((s, t) -> JinqStream.from(s.getUserMessageDeactivateList()),
                        (s, t) -> t.getUser().getId().equals(userId))
                .where(s -> s.getTwo() == null)
                .select(s -> s.getOne());
        var pageNum = stream.where(s -> s.getCreateDate().before(crateDate)).count()
                + stream.where(s -> crateDate.equals(s.getCreateDate())
                        && s.getId().compareTo(id) < 0).count()
                + 1;
        return pageNum;
    }
}
