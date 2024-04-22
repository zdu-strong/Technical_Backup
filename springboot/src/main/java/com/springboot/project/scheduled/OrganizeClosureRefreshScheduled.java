package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.project.common.DistributedExecutionTask.DistributedExecutionTaskUtil;
import com.springboot.project.service.DistributedExecutionService;
import com.springboot.project.service.OrganizeClosureService;

@Component
public class OrganizeClosureRefreshScheduled {

    @Autowired
    private OrganizeClosureService organizeClosureService;

    @Autowired
    private DistributedExecutionService distributedExecutionService;

    @Autowired
    private DistributedExecutionTaskUtil distributedExecutionTaskUtil;

    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 60 * 1000)
    public void scheduled() {
        this.refresh();
    }

    private void refresh() {
        while (true) {
            var distributedExecutionModel = this.distributedExecutionService.getDistributedExecutionOfOrganize();
            if (distributedExecutionModel == null) {
                return;
            }
            this.distributedExecutionTaskUtil.run(distributedExecutionModel.getId(), () -> {
                for (var organizeModel : distributedExecutionModel.getPagination().getList()) {
                    while (true) {
                        var hasNext = this.organizeClosureService.refresh(organizeModel.getId());
                        if (!hasNext) {
                            break;
                        }
                    }
                }
            });
        }
    }
}
