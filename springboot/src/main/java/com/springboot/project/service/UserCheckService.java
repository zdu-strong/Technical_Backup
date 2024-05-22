package com.springboot.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;

@Service
public class UserCheckService extends BaseService {

    public void checkExistAccount(String account) {
        if (!hasExistsUserId(account) || !hasExistEmail(account)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect username or password");
        }
    }

    private boolean hasExistsUserId(String userId) {
        var exists = this.UserEntity()
                .where(s -> s.getId().equals(userId))
                .where(s -> s.getIsActive())
                .exists();
        return exists;
    }

    private boolean hasExistEmail(String email) {
        var exists = this.UserEmailEntity()
                .where(s -> s.getEmail().equals(email))
                .where(s -> s.getIsActive())
                .where(s -> s.getUser().getIsActive())
                .exists();
        return exists;
    }

}
