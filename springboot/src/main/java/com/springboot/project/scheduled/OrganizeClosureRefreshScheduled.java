package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.DistributedExecutionUtil.DistributedExecutionUtil;
import com.springboot.project.enumerate.DistributedExecutionEnum;

@Component
public class OrganizeClosureRefreshScheduled {

    @Autowired
    private DistributedExecutionUtil distributedExecutionUtil;

    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 60 * 1000)
    public void scheduled() {
        this.refresh();
    }

    private void refresh() {
        while (true) {
            var isDone = this.distributedExecutionUtil
                    .run(DistributedExecutionEnum.ORGANIZE_REFRESH_ORGANIZE_CLOSURE_ENTITY);
            if (isDone) {
                return;
            }
        }
    }
}
