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
        var organizeModel = this.organizeFormatter.format(organizeEntity);
        var ancestorCount = this.OrganizeClosureEntity()
                .where(s -> s.getDescendant().getId().equals(organizeId))
                .count();
        if (ancestorCount <= organizeModel.getLevel()) {
            var ancestor = organizeEntity;
            while (true) {
                if (ancestor == null) {
                    break;
                }
                var ancestorId = ancestor.getId();
                var exists = this.OrganizeClosureEntity()
                        .where(s -> s.getAncestor().getId().equals(ancestorId))
                        .where(s -> s.getDescendant().getId().equals(organizeId))
                        .exists();
                if (!exists) {
                    this.create(ancestorId, organizeId);
                    return true;
                }
                ancestor = ancestor.getParent();
            }
        }

        {
            var ancestorList = this.OrganizeClosureEntity()
                    .where(s -> s.getDescendant().getId().equals(organizeId))
                    .toList();
            for (var organizeClosureEntity : ancestorList) {
                if (!this.organizeService.isChildOfOrganize(
                        organizeId, organizeClosureEntity.getAncestor().getId())) {
                    this.remove(organizeClosureEntity);
                    return true;
                }
            }
        }

        {
            var organizeClosureEntity = this.OrganizeClosureEntity()
                    .where(s -> s.getDescendant().getId().equals(organizeId))
                    .filter(s -> s.getIsActive() != isActive)
                    .findFirst()
                    .orElse(null);
            if (organizeClosureEntity != null) {
                organizeClosureEntity.setIsActive(isActive);
                organizeClosureEntity.setUpdateDate(new Date());
                this.merge(organizeClosureEntity);
                return true;
            }
        }

        return false;
    }

    private void create(String ancestorOrganizeId, String descendantOrganizeId) {
        var ancestor = this.OrganizeEntity().where(s -> s.getId().equals(ancestorOrganizeId)).getOnlyValue();
        var descendant = this.OrganizeEntity().where(s -> s.getId().equals(descendantOrganizeId)).getOnlyValue();
        var organizeClosureEntity = new OrganizeClosureEntity();
        organizeClosureEntity.setId(newId());
        organizeClosureEntity.setIsActive(true);
        organizeClosureEntity.setCreateDate(new Date());
        organizeClosureEntity.setUpdateDate(new Date());
        organizeClosureEntity.setAncestor(ancestor);
        organizeClosureEntity.setDescendant(descendant);
        this.persist(organizeClosureEntity);
    }

}
