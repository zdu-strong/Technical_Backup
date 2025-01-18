package com.springboot.project.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemPermissionEnum {

    SUPER_ADMIN_PERMISSION(true, false),
    ORGANIZE_MANAGE_PERMISSION(false, true),
    ORGANIZE_VIEW_PERMISSION(false, true);

    private Boolean isSuperAdmin;
    private Boolean isOrganizeRole;
}