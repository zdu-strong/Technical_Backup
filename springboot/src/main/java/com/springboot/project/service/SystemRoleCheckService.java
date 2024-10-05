package com.springboot.project.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;

@Service
public class SystemRoleCheckService extends BaseService {

    public void checkSystemRoleNameCannotBeBlank(String name) {
        if (StringUtils.isBlank(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "System role name cannot be empty");
        }
    }

}
