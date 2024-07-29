package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.OrganizeClosureEntity;

@Service
public class OrganizeClosureService extends BaseService {

    public boolean refresh(String organizeId) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var isActive = this.organizeFormatter.isActive(organizeEntity);

        if (!isActive) {
            var organizeClosureEntity = this.OrganizeClosureEntity()
                    .where(s -> s.getDescendant().getId().equals(organizeId))
                    .findFirst()
                    .orElse(null);
            if (organizeClosureEntity != null) {
                this.remove(organizeClosureEntity);
                return true;
            }
        }

        if (!isActive) {
            var organizeClosureEntity = this.OrganizeClosureEntity()
                    .where(s -> s.getAncestor().getId().equals(organizeId))
                    .findFirst()
                    .orElse(null);
            if (organizeClosureEntity != null) {
                this.remove(organizeClosureEntity);
                return true;
            }
        }

        if (!isActive) {
            return false;
        }

        var ancestorIdOneList = this.organizeFormatter.getAncestorIdList(organizeEntity);
        var ancestorIdTwoList = this.OrganizeClosureEntity()
                .where(s -> s.getDescendant().getId().equals(organizeId))
                .select(s -> s.getAncestor().getId())
                .toList();

        for (var ancestorId : ancestorIdTwoList) {
            if (ancestorIdOneList.contains(ancestorId)) {
                continue;
            }
            var organizeClosureEntity = this.OrganizeClosureEntity()
                    .where(s -> s.getAncestor().getId().equals(ancestorId))
                    .where(s -> s.getDescendant().getId().equals(organizeId))
                    .getOnlyValue();
            this.remove(organizeClosureEntity);
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
        var organizeClosureEntity = new OrganizeClosureEntity();
        organizeClosureEntity.setId(newId());
        organizeClosureEntity.setCreateDate(new Date());
        organizeClosureEntity.setUpdateDate(new Date());
        organizeClosureEntity.setAncestor(ancestor);
        organizeClosureEntity.setDescendant(descendant);
        this.persist(organizeClosureEntity);
    }

}
