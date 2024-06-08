package com.springboot.project.common.enumerate;

import java.time.Duration;

public class LongTermTaskTempWaitDurationEnum {
    /**
     * temp wait duration
     */
    public final static Duration TEMP_WAIT_DURATION = Duration.ofSeconds(30);

    public final static Long REFRESH_INTERVAL_SECOND = 10L;

    public final static Duration TEMP_TASK_SURVIVAL_DURATION = Duration.ofMinutes(1);
}
