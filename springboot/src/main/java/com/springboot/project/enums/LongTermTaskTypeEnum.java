package com.springboot.project.enums;

import java.util.Optional;
import cn.hutool.core.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LongTermTaskTypeEnum {

    COMMON("COMMON"),
    MOVE_ORGANIZE("MOVE_ORGANIZE"),
    INIT_SYSTEM_DATABASE_DATA("INIT_SYSTEM_DATABASE_DATA"),
    DISTRIBUTED_EXECUTION("DISTRIBUTED_EXECUTION"),
    CREATE_DISTRIBUTED_EXECUTION_MAIN("CREATE_DISTRIBUTED_EXECUTION_MAIN");

    public String value;

    public static LongTermTaskTypeEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(LongTermTaskTypeEnum::getValue, value)).get();
    }

}