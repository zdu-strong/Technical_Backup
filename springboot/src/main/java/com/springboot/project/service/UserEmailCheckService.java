package com.springboot.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;

@Service
public class UserEmailCheckService extends BaseService {

    public void checkEmailIsNotUsed(String email) {
        var isPresent = this.UserEmailEntity()
                .where(s -> s.getEmail().equals(email))
                .where(s -> !s.getIsDeleted())
                .exists();
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail " + email + " has bound account");
        }
    }
}
