package com.john.project.service;

import java.util.Date;

import com.john.project.entity.UserEntity;
import com.john.project.entity.UserMessageDeactivateEntity;
import com.john.project.entity.UserMessageEntity;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;

@Service
public class UserMessageDeactivateService extends BaseService {

    public void create(String userMessageId, String userId) {
        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .getOnlyValue();
        var userMessageEntity = this.streamAll(UserMessageEntity.class)
                .where(s -> s.getId().equals(userMessageId))
                .getOnlyValue();
        var userMessageDeactivateEntity = new UserMessageDeactivateEntity();
        userMessageDeactivateEntity.setId(newId());
        userMessageDeactivateEntity.setCreateDate(new Date());
        userMessageDeactivateEntity.setUpdateDate(new Date());
        userMessageDeactivateEntity.setUser(userEntity);
        userMessageDeactivateEntity.setUserMessage(userMessageEntity);
        this.persist(userMessageDeactivateEntity);
    }

}
