package com.springboot.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;

@Service
public class LongTermTaskCheckService extends BaseService {

    public void checkIsExistLongTermTaskById(String id) {
        var isExistLongTermTask = this.LongTermTaskEntity().where(s -> s.getId().equals(id)).exists();
        if (!isExistLongTermTask) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified task does not exist");
        }
    }

}
