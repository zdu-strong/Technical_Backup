package com.springboot.project.enums;

import java.util.Optional;
import cn.hutool.core.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DistributedExecutionMainStatusEnum {

    IN_PROGRESS("IN_PROGRESS"),
    SUCCESS_COMPLETE("SUCCESS_COMPLETE"),
    ERROR_END("ERROR_END"),
    ABORTED("ABORTED");

    private String value;

    public static DistributedExecutionMainStatusEnum parse(String value) {
        return Optional.ofNullable(EnumUtil.getBy(DistributedExecutionMainStatusEnum::getValue, value)).get();
    }

}