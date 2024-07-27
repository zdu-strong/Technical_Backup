package com.springboot.project.common.enumerate;

import lombok.Getter;

@Getter
public enum SystemRoleEnum {

    SUPER_ADMIN("SYSTEM_ROLE_SUPER_ADMIN", true, false),
    ORGANIZE_ADMIN("SYSTEM_ROLE_ORGANIZE_ADMIN", false, true),
    ORGANIZE_NORMAL_USER("SYSTEM_ROLE_ORGANIZE_NORMAL_USER", false, true);

    private String value;

    private Boolean isOrganizeRole;

    private Boolean isSuperAdmin;

    private SystemRoleEnum(String role, Boolean isSuperAdmin, Boolean isOrganizeRole) {
        this.value = role;
        this.isSuperAdmin = isSuperAdmin;
        this.isOrganizeRole = isOrganizeRole;
    }

}