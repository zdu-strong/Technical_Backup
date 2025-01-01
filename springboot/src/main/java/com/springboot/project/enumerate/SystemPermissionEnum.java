package com.springboot.project.enumerate;

import lombok.Getter;

@Getter
public enum SystemPermissionEnum {

    SUPER_ADMIN_PERMISSION(true, false),
    ORGANIZE_MANAGE_PERMISSION(false, true),
    ORGANIZE_VIEW_PERMISSION(false, true);

    private Boolean isOrganizeRole;

    private Boolean isSuperAdmin;

    private SystemPermissionEnum(Boolean isSuperAdmin, Boolean isOrganizeRole) {
        this.isSuperAdmin = isSuperAdmin;
        this.isOrganizeRole = isOrganizeRole;
    }

}