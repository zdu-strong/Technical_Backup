package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserMessageDeactivateEntity;

@Service
public class UserMessageDeactivateService extends BaseService {

    public void create(String userMessageId, String userId) {
        var userEntity = this.UserEntity().where(s -> s.getId().equals(userId)).getOnlyValue();
        var userMessageEntity = this.UserMessageEntity().where(s -> s.getId().equals(userMessageId)).getOnlyValue();
        var userMessageDeactivateEntity = new UserMessageDeactivateEntity();
        userMessageDeactivateEntity.setId(newId());
        userMessageDeactivateEntity.setCreateDate(new Date());
        userMessageDeactivateEntity.setUpdateDate(new Date());
        userMessageDeactivateEntity.setUser(userEntity);
        userMessageDeactivateEntity.setUserMessage(userMessageEntity);
        this.persist(userMessageDeactivateEntity);
    }

}
