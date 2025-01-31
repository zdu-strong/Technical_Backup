package com.springboot.project.enums;

import org.nd4j.common.primitives.Optional;
import cn.hutool.core.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemPermissionEnum {

    SUPER_ADMIN("SUPER_ADMIN", true, false),
    ORGANIZE_MANAGE("ORGANIZE_MANAGE", false, true),
    ORGANIZE_VIEW("ORGANIZE_VIEW", false, true);

    private String value;
    private Boolean isSuperAdmin;
    private Boolean isOrganizeRole;

    public static SystemPermissionEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(SystemPermissionEnum::getValue, value)).get();
    }
}