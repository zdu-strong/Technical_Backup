package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.common.mysql.MysqlFunction;
import com.springboot.project.entity.UserMessageEntity;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.service.BaseService;

@Service
public class UserMessageFormatter extends BaseService {

    public UserMessageModel format(UserMessageEntity userMessageEntity) {
        var user = this.userFormatter.format(userMessageEntity.getUser());
        var userMessage = new UserMessageModel().setId(userMessageEntity.getId())
                .setCreateDate(userMessageEntity.getCreateDate()).setUpdateDate(userMessageEntity.getUpdateDate())
                .setContent(userMessageEntity.getContent()).setIsDelete(userMessageEntity.getDeleteKey() != null)
                .setIsRecall(userMessageEntity.getIsRecall()).setUser(user);
        return userMessage;
    }

    public UserMessageModel formatForUserId(UserMessageEntity userMessageEntity, String userId) {
        var userMessage = this.format(userMessageEntity);
        userMessage.setTotalPage(this.UserMessageEntity().count());
        var crateDate = userMessage.getCreateDate();
        var id = userMessage.getId();
        var pageNum = this.UserMessageEntity()
                .where(s -> crateDate.after(s.getCreateDate())
                        || (crateDate.equals(s.getCreateDate()) && Boolean.valueOf(true).equals(MysqlFunction.isSortAtBefore(s.getId(), id)) ))
                .count();
        userMessage.setPageNum(Long.valueOf(pageNum).intValue() + 1);
        if (!userMessage.getUser().getId().equals(userId)) {
            userMessage.setIsDelete(false);
        }
        return userMessage;
    }
}
