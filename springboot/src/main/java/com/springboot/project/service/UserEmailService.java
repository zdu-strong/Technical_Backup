package com.springboot.project.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.UserEmailEntity;

import cn.hutool.core.lang.Validator;

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

    public void checkCorrectFormatOfEmail(String email) {
        if (!Validator.isEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
        }
    }

    public void checkCannotEmptyOfEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
        }
    }

    public void checkIsNotUsedOfEmail(String email) {
        var isPresent = this.UserEmailEntity()
                .where(s -> s.getEmail().equals(email))
                .where(s -> s.getIsActive())
                .where(s -> s.getUser().getIsActive())
                .exists();
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail " + email + " has bound account");
        }
    }

}
