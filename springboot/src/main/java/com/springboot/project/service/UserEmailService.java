package com.springboot.project.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEmailEntity;

@Service
public class UserEmailService extends BaseService {

    public void createUserEmail(String email, String userId) {
        inactiveUserEmail(email);

        var userEntity = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .where(s -> s.getIsActive())
                .getOnlyValue();
        UserEmailEntity userEmailEntity = new UserEmailEntity();
        userEmailEntity.setId(newId());
        userEmailEntity.setEmail(email);
        userEmailEntity.setUser(userEntity);
        userEmailEntity.setCreateDate(new Date());
        userEmailEntity.setUpdateDate(new Date());
        userEmailEntity.setIsActive(true);
        userEmailEntity.setDeactiveKey("");

        this.persist(userEmailEntity);
    }

    private void inactiveUserEmail(String email) {
        var userEmailList = this.UserEmailEntity()
                .where(s -> s.getIsActive())
                .where(s -> !s.getUser().getIsActive())
                .toList();
        for (var userEmailEntity : userEmailList) {
            userEmailEntity.setIsActive(false);
            userEmailEntity.setDeactiveKey(Generators.timeBasedReorderedGenerator().generate().toString());
            userEmailEntity.setUpdateDate(new Date());
            this.merge(userEmailEntity);
        }
    }

}
