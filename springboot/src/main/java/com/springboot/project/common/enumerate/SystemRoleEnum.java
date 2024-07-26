package com.springboot.project.common.enumerate;

import lombok.Getter;

@Getter
public enum SystemRoleEnum {

    SUPER_ADMIN("SUPER_ADMIN", true, false),
    COMPANY_ADMIN("COMPANY_ADMIN", false, true),
    ORGANIZE_ADMIN("ORGANIZE_ADMIN", false, true);

    private String value;

    private Boolean isOrganizeRole;

    private Boolean isSuperAdmin;

    private SystemRoleEnum(String role, Boolean isSuperAdmin, Boolean isOrganizeRole) {
        this.value = role;
        this.isSuperAdmin = isSuperAdmin;
        this.isOrganizeRole = isOrganizeRole;
    }

}