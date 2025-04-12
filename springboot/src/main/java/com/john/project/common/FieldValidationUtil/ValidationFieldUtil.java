package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.lang.Validator;
import com.john.project.model.RoleModel;
import com.john.project.model.UserMessageModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ValidationFieldUtil {

    public void checkNotBlankOfUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username cannot be blank");
        }
    }

    public void checkNotBlankOfNickname(String nickname) {
        if (StringUtils.isBlank(nickname)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nickname cannot be blank");
        }
    }

    public void checkNotEdgesSpaceOfUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return;
        }
        if (username.trim().length() != username.length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }
    }

    public void checkNotBlankOfUserMessageContent(UserMessageModel userMessageModel) {
        if (StringUtils.isBlank(userMessageModel.getUrl()) && StringUtils.isBlank(userMessageModel.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message cannot be blank");
        }
    }

    public void checkNotBlankOfPassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be blank");
        }
    }

    public void checkNotBlankOfRoleName(String roleName) {
        if (StringUtils.isBlank(roleName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleName cannot be blank");
        }
    }

    public void checkNotEmptyOfPermissionList(RoleModel roleModel) {
        if (CollectionUtils.isEmpty(roleModel.getPermissionList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "permissionList cannot be empty");
        }
    }

    public void checkNotBlankOfId(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id cannot be blank");
        }
    }

    public void checkNotBlankOfEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email cannot be blank");
        }
    }

    public void checkCorrectFormatOfEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return;
        }
        if (!Validator.isEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
    }


}
