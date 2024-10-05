package com.springboot.project.common.OrganizeUtil;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.springboot.project.common.longtermtask.LongTermTaskUtil;
import com.springboot.project.enumerate.LongTermTaskTypeEnum;
import com.springboot.project.model.LongTermTaskUniqueKeyModel;
import com.springboot.project.service.OrganizeCheckService;
import com.springboot.project.service.OrganizeRelationService;
import com.springboot.project.service.OrganizeService;

@Component
public class OrganizeUtil {

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private OrganizeRelationService organizeRelationService;

    @Autowired
    private OrganizeCheckService organizeCheckService;

    @Autowired
    private LongTermTaskUtil longTermTaskUtil;

    @Autowired
    private ObjectMapper objectMapper;

    public void move(String id, String parentId) {
        var expectException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Too many requests to move the organize, please wait a minute and try again");
        var uniqueKeyList = this.getUniqueKeyList(id, parentId);
        this.longTermTaskUtil.run(() -> {
            this.checkUniqueKeyList(id, parentId, uniqueKeyList);
            this.organizeCheckService.checkOrganizeCanBeMove(id, parentId);
            this.organizeService.move(id, parentId);
        }, true, expectException, uniqueKeyList);

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

    private void checkUniqueKeyList(String id, String parentId, LongTermTaskUniqueKeyModel[] uniqueKeyList) {
        var uniqueKeyListTwo = this.getUniqueKeyList(id, parentId);
        try {
            if (!this.objectMapper.writeValueAsString(uniqueKeyList)
                    .equals(this.objectMapper.writeValueAsString(uniqueKeyListTwo))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Too many requests to move the organize, please wait a minute and try again");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private LongTermTaskUniqueKeyModel[] getUniqueKeyList(String id, String parentId) {
        var uniqueKeyList = Lists.newArrayList(id, parentId)
                .stream()
                .filter(s -> StringUtils.isNotBlank(s))
                .map(s -> this.organizeService.getTopOrganize(s))
                .map(s -> {
                    var longTermTaskUniqueKey = new LongTermTaskUniqueKeyModel();
                    longTermTaskUniqueKey.setType(LongTermTaskTypeEnum.MOVE_ORGANIZE.getType());
                    longTermTaskUniqueKey.setUniqueKey(s.getId());
                    return longTermTaskUniqueKey;
                })
                .toList()
                .toArray(new LongTermTaskUniqueKeyModel[] {});
        return uniqueKeyList;
    }

}
