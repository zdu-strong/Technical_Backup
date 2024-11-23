package com.springboot.project.enumerate;

import lombok.Getter;

@Getter
public enum SystemRoleEnum {

    SUPER_ADMIN(true, false),
    ORGANIZE_ADMIN(false, true),
    ORGANIZE_NORMAL_USER(false, true);

    private Boolean isOrganizeRole;

    private Boolean isSuperAdmin;

    private SystemRoleEnum(Boolean isSuperAdmin, Boolean isOrganizeRole) {
        this.isSuperAdmin = isSuperAdmin;
        this.isOrganizeRole = isOrganizeRole;
    }

}