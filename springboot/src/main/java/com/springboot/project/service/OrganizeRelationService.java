package com.springboot.project.service;

import java.util.Date;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.enumerate.DatabaseBatchEnum;

@Service
public class OrganizeRelationService extends BaseService {

    public boolean refresh(String organizeId) {
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var isActive = this.organizeFormatter.isActive(organizeEntity);

        if (!isActive) {
            var organizeRelationEntityList = this.streamAll(OrganizeRelationEntity.class)
                    .where(s -> s.getAncestor().getId().equals(organizeId))
                    .limit(DatabaseBatchEnum.batchSize)
                    .toList();
            var hasNext = organizeRelationEntityList.size() == DatabaseBatchEnum.batchSize;
            for (var organizeRelationEntity : organizeRelationEntityList) {
                this.remove(organizeRelationEntity);
            }
            return hasNext;
        }

        var ancestorIdOneList = this.organizeFormatter.getAncestorIdList(organizeEntity);
        var ancestorIdTwoList = this.streamAll(OrganizeRelationEntity.class)
                .where(s -> s.getDescendant().getId().equals(organizeId))
                .select(s -> s.getAncestor().getId())
                .toList();
        var organizeRelationListWillRemove = JinqStream.from(ancestorIdTwoList)
                .where(s -> !ancestorIdOneList.contains(s))
                .limit(DatabaseBatchEnum.batchSize)
                .map(ancestorId -> this.streamAll(OrganizeRelationEntity.class)
                        .where(s -> s.getAncestor().getId().equals(ancestorId))
                        .where(s -> s.getDescendant().getId().equals(organizeId))
                        .getOnlyValue())
                .toList();
        var ancestorIdList = JinqStream.from(ancestorIdOneList)
                .where(s -> !ancestorIdTwoList.contains(s))
                .limit(DatabaseBatchEnum.batchSize)
                .toList();
        var totalTimes = DatabaseBatchEnum.batchSize;
        for (var organizeRelationEntity : organizeRelationListWillRemove) {
            if (totalTimes == 0) {
                break;
            }
            totalTimes--;
            this.remove(organizeRelationEntity);
        }
        for (var ancestorId : ancestorIdList) {
            if (totalTimes == 0) {
                break;
            }
            totalTimes--;
            this.create(ancestorId, organizeId);
        }
        var hasNext = totalTimes == 0;
        return hasNext;
    }

    private void create(String ancestorId, String descendantId) {
        var ancestor = this.streamAll(OrganizeEntity.class).where(s -> s.getId().equals(ancestorId)).getOnlyValue();
        var descendant = this.streamAll(OrganizeEntity.class).where(s -> s.getId().equals(descendantId)).getOnlyValue();
        var organizeRelationEntity = new OrganizeRelationEntity();
        organizeRelationEntity.setId(newId());
        organizeRelationEntity.setCreateDate(new Date());
        organizeRelationEntity.setUpdateDate(new Date());
        organizeRelationEntity.setAncestor(ancestor);
        organizeRelationEntity.setDescendant(descendant);
        this.persist(organizeRelationEntity);
    }

}
