package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.springboot.project.enumerate.DistributedExecutionEnum;

@Component
public class NonceScheduled {

    @Autowired
    private DistributedExecutionUtil distributedExecutionUtil;

    @Scheduled(initialDelay = 12 * 60 * 60 * 1000, fixedDelay = 12 * 60 * 60 * 1000)
    public void scheduled() {

        this.distributedExecutionUtil.refreshData(DistributedExecutionEnum.NONCE_CLEAN);
    }

}
