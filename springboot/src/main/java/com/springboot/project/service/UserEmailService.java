package com.springboot.project.service;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import cn.hutool.core.lang.Validator;

@Service
public class UserEmailService extends BaseService {

    public void createUserEmail(String email, String userId) {
        inactiveUserEmail(email);

        var userEntity = this.streamAll(UserEntity.class)
                .where(s -> s.getId().equals(userId))
                .where(s -> !s.getIsDeleted())
                .getOnlyValue();
        UserEmailEntity userEmailEntity = new UserEmailEntity();
        userEmailEntity.setId(newId());
        userEmailEntity.setEmail(email);
        userEmailEntity.setUser(userEntity);
        userEmailEntity.setCreateDate(new Date());
        userEmailEntity.setUpdateDate(new Date());
        userEmailEntity.setIsDeleted(false);
        userEmailEntity.setDeletionCode(StringUtils.EMPTY);

        this.persist(userEmailEntity);
    }

    private void inactiveUserEmail(String email) {
        var userEmailList = this.streamAll(UserEmailEntity.class)
                .where(s -> !s.getIsDeleted())
                .where(s -> s.getUser().getIsDeleted())
                .toList();
        for (var userEmailEntity : userEmailList) {
            userEmailEntity.setIsDeleted(true);
            userEmailEntity.setDeletionCode(Generators.timeBasedReorderedGenerator().generate().toString());
            userEmailEntity.setUpdateDate(new Date());
            this.merge(userEmailEntity);
        }
    }

    @Transactional(readOnly = true)
    public void checkCorrectFormatOfEmail(String email) {
        if (!Validator.isEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
        }
    }

    @Transactional(readOnly = true)
    public void checkCannotEmptyOfEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
        }
    }

    @Transactional(readOnly = true)
    public void checkIsNotUsedOfEmail(String email) {
        var isPresent = this.streamAll(UserEmailEntity.class)
                .where(s -> s.getEmail().equals(email))
                .where(s -> !s.getIsDeleted())
                .where(s -> !s.getUser().getIsDeleted())
                .exists();
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail " + email + " has bound account");
        }
    }

}
