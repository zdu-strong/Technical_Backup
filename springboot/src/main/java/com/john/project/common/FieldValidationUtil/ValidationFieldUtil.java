package com.john.project.common.FieldValidationUtil;

import cn.hutool.core.lang.Validator;
import com.john.project.model.RoleModel;
import com.john.project.model.UserMessageModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
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

    public void checkNotBlankOfUserMessageContent(UserMessageModel userMessageModel) {
        if (StringUtils.isBlank(userMessageModel.getUrl()) && StringUtils.isBlank(userMessageModel.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in the message content");
        }
    }

    public void checkNotBlankOfPassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in password");
        }
    }

    public void checkNotBlankOfRoleName(String roleName) {
        if (StringUtils.isBlank(roleName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in roleName");
        }
    }

    public void checkNotEmptyOfPermissionList(RoleModel roleModel) {
        if (CollectionUtils.isEmpty(roleModel.getPermissionList())) {
            roleModel.setPermissionList(List.of());
        }
        if (CollectionUtils.isEmpty(roleModel.getOrganizeList())) {
            roleModel.setOrganizeList(List.of());
        }
        if (roleModel.getPermissionList().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission list cannot be empty");
        }
    }

    public void checkNotBlankOfId(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id cannot be null");
        }
    }

    public void checkNotBlankOfEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
        }
    }

    public void checkCorrectFormatOfEmail(String email) {
        if(StringUtils.isEmpty(email)){
            return;
        }
        if (!Validator.isEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
        }
    }


}
