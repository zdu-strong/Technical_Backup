package com.springboot.project.format;

import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.model.UserMessageModel;

@Service
public class UserMessageFormatter extends BaseService {

    public UserMessageModel format(UserMessageEntity userMessageEntity) {
        var userMessageModel = new UserMessageModel();
        BeanUtils.copyProperties(userMessageEntity, userMessageModel);
        userMessageModel.setIsDeleted(false);
        userMessageModel.setUser(this.userFormatter.format(userMessageEntity.getUser()));
        if (!userMessageModel.getIsRecall() && StringUtils.isNotBlank(userMessageEntity.getFolderName())) {
            userMessageModel
                    .setUrl(this.storage.getResoureUrlFromResourcePath(
                            Paths.get(userMessageEntity.getFolderName(), userMessageEntity.getFileName()).toString()));
        }
        if (userMessageModel.getIsRecall() || StringUtils.isNotBlank(userMessageEntity.getFolderName())) {
            userMessageModel.setContent("");
        }
        return userMessageModel;
    }

    public UserMessageModel formatForUserId(UserMessageEntity userMessageEntity, String userId) {
        var userMessage = this.format(userMessageEntity);
        userMessage.setTotalPage(this.UserMessageEntity().where(s -> !s.getIsRecall()).count());
        var crateDate = userMessage.getCreateDate();
        var id = userMessage.getId();
        var pageNum = this.UserMessageEntity()
                .where(s -> !s.getIsRecall())
                .where(s -> crateDate.after(s.getCreateDate())
                        || (crateDate.equals(s.getCreateDate())
                                && JPQLFunction.isSortAtBefore(s.getId(), id)))
                .count();
        userMessage.setPageNum(pageNum + 1);
        if (!userMessage.getUser().getId().equals(userId)) {
            userMessage.setIsDeleted(false);
        }
        return userMessage;
    }
}
