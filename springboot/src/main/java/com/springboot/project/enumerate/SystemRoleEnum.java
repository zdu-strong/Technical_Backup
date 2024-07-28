package com.springboot.project.enumerate;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum SystemRoleEnum {

    SUPER_ADMIN("SYSTEM_ROLE_SUPER_ADMIN", true, false),
    ORGANIZE_ADMIN("SYSTEM_ROLE_ORGANIZE_ADMIN", false, true),
    ORGANIZE_NORMAL_USER("SYSTEM_ROLE_ORGANIZE_NORMAL_USER", false, true);

    private String role;

    private Boolean isOrganizeRole;

    private Boolean isSuperAdmin;

    private SystemRoleEnum(String role, Boolean isSuperAdmin, Boolean isOrganizeRole) {
        this.role = role;
        this.isSuperAdmin = isSuperAdmin;
        this.isOrganizeRole = isOrganizeRole;
    }

    public static SystemRoleEnum valueOfRole(String role) {
        return Arrays.stream(SystemRoleEnum.values()).filter(s -> s.getRole().equals(role)).findFirst().get();
    }

}