package com.john.project.common.FieldValidationUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ValidationFieldUtilNotEdgesSpace extends ValidationFieldUtilNotEmpty {

    public void checkNotEdgesSpaceOfUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return;
        }
        if (username.trim().length() != username.length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }
    }

}
