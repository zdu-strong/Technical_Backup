package com.springboot.project.common.OrganizeUtil;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.model.OrganizeMoveTopModel;
import com.springboot.project.service.OrganizeCheckService;
import com.springboot.project.service.OrganizeRelationService;
import com.springboot.project.service.OrganizeMoveTopService;
import com.springboot.project.service.OrganizeService;

@Component
public class OrganizeUtil {

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private OrganizeRelationService organizeRelationService;

    @Autowired
    private OrganizeMoveTopService organizeMoveTopService;

    @Autowired
    private OrganizeCheckService organizeCheckService;

    public void move(String id, String parentId) throws InterruptedException {
        OrganizeMoveTopModel[] organizeMoveTopList;
        var initStartDate = new Date();
        while (true) {
            try {
                organizeMoveTopList = this.organizeMoveTopService.createOrganizeMoveTop(id, parentId);
                break;
            } catch (DataIntegrityViolationException e) {
                if (!initStartDate.before(DateUtils.addSeconds(new Date(), -10))) {
                    Thread.sleep(1);
                    continue;
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Too many requests to move the organize, please wait a minute and try again");
            }
        }
        try {
            this.organizeCheckService.checkOrganizeCanBeMove(id, parentId);
            this.organizeService.move(id, parentId);
        } finally {
            this.organizeMoveTopService.deleteOrganizeMoveTop(organizeMoveTopList);
        }

        this.refresh(id);
    }

    public void refresh(String organizeId) {
        var deadline = this.getDeadline();
        var maxDeep = 1000L;
        this.refresh(organizeId, deadline, maxDeep);
    }

    private void refresh(String organizeId, Date deadline, Long maxDeep) {
        if (maxDeep <= 0) {
            return;
        }

        while (true) {
            if (new Date().after(deadline)) {
                return;
            }
            var hasNext = this.organizeRelationService.refresh(organizeId);
            if (!hasNext) {
                break;
            }
        }

        var childIdList = this.organizeService.getChildIdList(organizeId);

        for (var childId : childIdList) {
            if (new Date().after(deadline)) {
                return;
            }
            this.refresh(childId, deadline, maxDeep - 1);
        }
    }

    private Date getDeadline() {
        return DateUtils.addSeconds(new Date(), 10);
    }

}
