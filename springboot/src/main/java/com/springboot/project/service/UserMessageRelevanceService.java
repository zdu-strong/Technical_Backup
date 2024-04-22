package com.springboot.project.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserMessageRelevanceEntity;

@Service
public class UserMessageRelevanceService extends BaseService {

    public void create(String userMessageId, String userId, boolean isDeleted){
        var userEntity = this.UserEntity().where(s->s.getId().equals(userId)).getOnlyValue();
        var userMessageEntity = this.UserMessageEntity().where(s->s.getId().equals(userMessageId)).getOnlyValue();
        var userMessageRelevanceEntity = new UserMessageRelevanceEntity();
        userMessageRelevanceEntity.setId(newId());
        userMessageRelevanceEntity.setIsDeleted(isDeleted);
        userMessageRelevanceEntity.setCreateDate(new Date());
        userMessageRelevanceEntity.setUpdateDate(new Date());
        userMessageRelevanceEntity.setUser(userEntity);
        userMessageRelevanceEntity.setUserMessage(userMessageEntity);
        this.persist(userMessageRelevanceEntity);
    }

}
