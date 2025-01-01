package com.springboot.project.enumerate;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum SystemRoleEnum {

    SUPER_ADMIN(false, SystemPermissionEnum.SUPER_ADMIN_PERMISSION),
    ORGANIZE_MANAGE(true, SystemPermissionEnum.ORGANIZE_MANAGE_PERMISSION),
    ORGANIZE_VIEW(true, SystemPermissionEnum.ORGANIZE_VIEW_PERMISSION);

    private Boolean isOrganizeRole;

    private List<SystemPermissionEnum> permissionList;

    private SystemRoleEnum(boolean isOrganizeRole, SystemPermissionEnum... permissionArray) {
        this.isOrganizeRole = isOrganizeRole;
        this.permissionList = Arrays.asList(permissionArray);
    }

}