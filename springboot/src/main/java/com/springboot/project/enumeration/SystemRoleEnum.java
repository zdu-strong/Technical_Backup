package com.springboot.project.enumeration;

import java.util.Arrays;
import java.util.List;
import org.jinq.orm.stream.JinqStream;
import lombok.Getter;

@Getter
public enum SystemRoleEnum {

    SUPER_ADMIN("SUPER_ADMIN", false, SystemPermissionEnum.SUPER_ADMIN_PERMISSION),
    ORGANIZE_MANAGE("ORGANIZE_MANAGE", true, SystemPermissionEnum.ORGANIZE_MANAGE_PERMISSION),
    ORGANIZE_VIEW("ORGANIZE_VIEW", true, SystemPermissionEnum.ORGANIZE_VIEW_PERMISSION);

    private String value;

    private Boolean isOrganizeRole;

    private List<SystemPermissionEnum> permissionList;

    private SystemRoleEnum(String value, boolean isOrganizeRole, SystemPermissionEnum... permissionArray) {
        this.value = value;
        this.isOrganizeRole = isOrganizeRole;
        this.permissionList = Arrays.asList(permissionArray);
    }

    public static SystemRoleEnum parseValue(String value) {
        return JinqStream.from(List.of(SystemRoleEnum.values()))
                .where(s -> s.getValue().equals(value))
                .getOnlyValue();
    }

}