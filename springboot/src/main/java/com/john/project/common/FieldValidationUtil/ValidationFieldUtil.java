package com.john.project.common.FieldValidationUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
public class ValidationFieldUtil {

    public void checkNotBlankOfUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in nickname");
        }
    }

    public void checkNotEdgesSpaceOfUsername(String username) {
        if (Objects.isNull(username)) {
            return;
        }
        if (username.trim().length() != username.length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }
    }
}
