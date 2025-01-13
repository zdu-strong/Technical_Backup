package com.springboot.project.enumerate;

import lombok.Getter;

@Getter
public enum LongTermTaskTypeEnum {

    COMMON,
    MOVE_ORGANIZE,
    INIT_SYSTEM_DATABASE_DATA,
    DISTRIBUTED_EXECUTION;
}