package com.springboot.project.enums;

import java.util.List;
import java.util.Optional;
import cn.hutool.core.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemRoleEnum {

    SUPER_ADMIN("SUPER_ADMIN", false, List.of(SystemPermissionEnum.SUPER_ADMIN)),
    ORGANIZE_MANAGE("ORGANIZE_MANAGE", true, List.of(SystemPermissionEnum.ORGANIZE_MANAGE)),
    ORGANIZE_VIEW("ORGANIZE_VIEW", true, List.of(SystemPermissionEnum.ORGANIZE_VIEW));

    private String value;

    private Boolean isOrganizeRole;

    private List<SystemPermissionEnum> permissionList;

    public static SystemRoleEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(SystemRoleEnum::getValue, value)).get();
    }

}