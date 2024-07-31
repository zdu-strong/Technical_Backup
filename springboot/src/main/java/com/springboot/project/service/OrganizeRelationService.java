package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.OrganizeRelationEntity;

@Service
public class OrganizeRelationService extends BaseService {

    public boolean refresh(String organizeId) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var isActive = this.organizeFormatter.isActive(organizeEntity);

        if (!isActive) {
            var organizeRelationEntity = this.OrganizeRelationEntity()
                    .where(s -> s.getDescendant().getId().equals(organizeId))
                    .findFirst()
                    .orElse(null);
            if (organizeRelationEntity != null) {
                this.remove(organizeRelationEntity);
                return true;
            }
        }

        if (!isActive) {
            var organizeRelationEntity = this.OrganizeRelationEntity()
                    .where(s -> s.getAncestor().getId().equals(organizeId))
                    .findFirst()
                    .orElse(null);
            if (organizeRelationEntity != null) {
                this.remove(organizeRelationEntity);
                return true;
            }
        }

        if (!isActive) {
            return false;
        }

        var ancestorIdOneList = this.organizeFormatter.getAncestorIdList(organizeEntity);
        var ancestorIdTwoList = this.OrganizeRelationEntity()
                .where(s -> s.getDescendant().getId().equals(organizeId))
                .select(s -> s.getAncestor().getId())
                .toList();

        for (var ancestorId : ancestorIdTwoList) {
            if (ancestorIdOneList.contains(ancestorId)) {
                continue;
            }
            var organizeRelationEntity = this.OrganizeRelationEntity()
                    .where(s -> s.getAncestor().getId().equals(ancestorId))
                    .where(s -> s.getDescendant().getId().equals(organizeId))
                    .getOnlyValue();
            this.remove(organizeRelationEntity);
            return true;
        }

        for (var ancestorId : ancestorIdOneList) {
            if (ancestorIdTwoList.contains(ancestorId)) {
                continue;
            }
            this.create(ancestorId, organizeId);
            return true;
        }

        return false;
    }

    private void create(String ancestorId, String descendantId) {
        var ancestor = this.OrganizeEntity().where(s -> s.getId().equals(ancestorId)).getOnlyValue();
        var descendant = this.OrganizeEntity().where(s -> s.getId().equals(descendantId)).getOnlyValue();
        var organizeRelationEntity = new OrganizeRelationEntity();
        organizeRelationEntity.setId(newId());
        organizeRelationEntity.setCreateDate(new Date());
        organizeRelationEntity.setUpdateDate(new Date());
        organizeRelationEntity.setAncestor(ancestor);
        organizeRelationEntity.setDescendant(descendant);
        this.persist(organizeRelationEntity);
    }

}
