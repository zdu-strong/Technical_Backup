package com.springboot.project.enumeration;

import java.util.List;
import org.jinq.orm.stream.JinqStream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemPermissionEnum {

    SUPER_ADMIN_PERMISSION("SUPER_ADMIN", true, false),
    ORGANIZE_MANAGE_PERMISSION("ORGANIZE_MANAGE", false, true),
    ORGANIZE_VIEW_PERMISSION("ORGANIZE_VIEW", false, true);

    private String value;
    private Boolean isSuperAdmin;
    private Boolean isOrganizeRole;

    public static SystemPermissionEnum parseValue(String value) {
        return JinqStream.from(List.of(SystemPermissionEnum.values()))
                .where(s -> s.getValue().equals(value))
                .getOnlyValue();
    }
}