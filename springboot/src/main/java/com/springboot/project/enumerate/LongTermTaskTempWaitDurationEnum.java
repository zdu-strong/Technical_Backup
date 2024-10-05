package com.springboot.project.enumerate;

import java.time.Duration;

public class LongTermTaskTempWaitDurationEnum {

    public final static Duration REFRESH_INTERVAL_DURATION = Duration.ofSeconds(10);

    public final static Duration TEMP_TASK_SURVIVAL_DURATION = Duration.ofMinutes(1);
}
