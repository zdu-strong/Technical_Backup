package com.john.project.service;

import java.util.Date;

import com.john.project.entity.OrganizeEntity;
import com.john.project.entity.OrganizeRelationEntity;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.*;

@Service
public class OrganizeRelationService extends BaseService {

    public boolean refresh(String organizeId) {
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var isActive = this.organizeFormatter.isActive(organizeEntity);

        if (!isActive) {
            var organizeRelationEntity = this.streamAll(OrganizeRelationEntity.class)
                    .where(s -> s.getAncestor().getId().equals(organizeId))
                    .findFirst()
                    .orElse(null);
            if (organizeRelationEntity != null) {
                this.remove(organizeRelationEntity);
            }
            var hasNext = organizeRelationEntity != null;
            return hasNext;
        }

        var ancestorIdOneList = this.organizeFormatter.getAncestorIdList(organizeEntity);
        var ancestorIdTwoList = this.streamAll(OrganizeRelationEntity.class)
                .where(s -> s.getDescendant().getId().equals(organizeId))
                .select(s -> s.getAncestor().getId())
                .toList();
        {
            var organizeRelationEntity = JinqStream.from(ancestorIdTwoList)
                    .where(s -> !ancestorIdOneList.contains(s))
                    .findFirst()
                    .map(ancestorId -> this.streamAll(OrganizeRelationEntity.class)
                            .where(s -> s.getAncestor().getId().equals(ancestorId))
                            .where(s -> s.getDescendant().getId().equals(organizeId))
                            .getOnlyValue())
                    .orElse(null);
            if (organizeRelationEntity != null) {
                this.remove(organizeRelationEntity);
                return true;
            }
        }
        {
            var ancestorId = JinqStream.from(ancestorIdOneList)
                    .where(s -> !ancestorIdTwoList.contains(s))
                    .findFirst()
                    .orElse(null);
            if (StringUtils.isNotBlank(ancestorId)) {
                this.create(ancestorId, organizeId);
                return true;
            }
        }

        return false;
    }

    private void create(String ancestorId, String descendantId) {
        var ancestor = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(ancestorId))
                .getOnlyValue();
        var descendant = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(descendantId))
                .getOnlyValue();
        var organizeRelationEntity = new OrganizeRelationEntity();
        organizeRelationEntity.setId(newId());
        organizeRelationEntity.setCreateDate(new Date());
        organizeRelationEntity.setUpdateDate(new Date());
        organizeRelationEntity.setAncestor(ancestor);
        organizeRelationEntity.setDescendant(descendant);
        this.persist(organizeRelationEntity);
    }

}
