package com.springboot.project.enumerate;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum LongTermTaskTypeEnum {

    COMMON("COMMON"),
    MOVE_ORGANIZE("MOVE_ORGANIZE"),
    INIT_SYSTEM_ROLE("INIT_SYSTEM_ROLE"),
    STORAGE_SPACE_CLEAN_DATABASE_STORAGE("STORAGE_SPACE_CLEAN_DATABASE_STORAGE"),
    ORGANIZE_REFRESH_ORGANIZE_CLOSURE_ENTITY("ORGANIZE_REFRESH_ORGANIZE_CLOSURE_ENTITY");

    private String type;

    private LongTermTaskTypeEnum(String type) {
        this.type = type;
    }

    public static LongTermTaskTypeEnum valueOfName(String type) {
        return Arrays.stream(LongTermTaskTypeEnum.values()).filter(s -> s.getType().equals(type)).findFirst().get();
    }

}