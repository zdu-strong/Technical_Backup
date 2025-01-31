package com.springboot.project.enumeration;

import java.util.List;
import org.jinq.orm.stream.JinqStream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public enum LongTermTaskTypeEnum {

    COMMON("COMMON"),
    MOVE_ORGANIZE("MOVE_ORGANIZE"),
    INIT_SYSTEM_DATABASE_DATA("INIT_SYSTEM_DATABASE_DATA"),
    DISTRIBUTED_EXECUTION("DISTRIBUTED_EXECUTION");

    public String value;

    public static LongTermTaskTypeEnum parseValue(String value) {
        return JinqStream.from(List.of(LongTermTaskTypeEnum.values()))
                .where(s -> s.getValue().equals(value))
                .getOnlyValue();
    }

}