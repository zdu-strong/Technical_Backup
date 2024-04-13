package com.springboot.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;

@Service
public class UserCheckService extends BaseService {

    public void checkExistAccount(String account) {
        var userId = account;
        var email = account;
        var stream = this.UserEntity().leftOuterJoinList(s -> s.getUserEmailList())
                .where(s -> s.getOne().getId().equals(userId)
                        || (s.getTwo().getEmail().equals(email) && !s.getTwo().getIsDeleted()))
                .where(s -> !s.getOne().getIsDeleted())
                .group(s -> s.getOne().getId(), (s, t) -> t.count());
        if (!stream.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect username or password");
        }
    }

}
