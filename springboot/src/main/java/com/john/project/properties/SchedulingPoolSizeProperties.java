package com.john.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class SchedulingPoolSizeProperties {

    @Value("${spring.task.scheduling.pool.size}")
    private Long schedulingPoolSize;

}
