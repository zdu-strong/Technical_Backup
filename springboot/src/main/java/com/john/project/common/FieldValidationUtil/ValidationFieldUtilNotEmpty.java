package com.john.project.common.FieldValidationUtil;

import com.john.project.model.RoleModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ValidationFieldUtilNotEmpty extends ValidationFieldUtilNotBlank {

    public void checkNotEmptyOfPermissionList(RoleModel roleModel) {
        if (CollectionUtils.isEmpty(roleModel.getPermissionList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "permissionList cannot be empty");
        }
    }

}
