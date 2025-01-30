package com.springboot.project.enumerate;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LongTermTaskTypeEnum {

    COMMON("COMMON"),
    MOVE_ORGANIZE("MOVE_ORGANIZE"),
    INIT_SYSTEM_DATABASE_DATA("INIT_SYSTEM_DATABASE_DATA"),
    DISTRIBUTED_EXECUTION("DISTRIBUTED_EXECUTION");

    @JsonValue
    private String value;

}