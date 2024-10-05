package com.springboot.project.format;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;

@Service
public class OrganizeFormatter extends BaseService {

    public OrganizeModel format(OrganizeEntity organizeEntity) {
        var organizeModel = new OrganizeModel();
        BeanUtils.copyProperties(organizeEntity, organizeModel);
        organizeModel.setChildCount(0L)
                .setDescendantCount(0L)
                .setChildList(Lists.newArrayList());

        var id = organizeEntity.getId();

        organizeModel.setLevel(this.getLevel(organizeEntity));

        if (organizeEntity.getParent() != null) {
            organizeModel.setParent(new OrganizeModel().setId(organizeEntity.getParent().getId()));
        }

        if (this.isActive(organizeEntity)) {
            var childOrganizeCount = this.OrganizeEntity()
                    .where(s -> s.getParent().getId().equals(id))
                    .where(s -> s.getIsActive())
                    .count();
            organizeModel.setChildCount(childOrganizeCount);
            var descendantCount = Math.max(this.OrganizeRelationEntity()
                    .where(s -> s.getAncestor().getId().equals(id))
                    .count(), 1) - 1;
            organizeModel.setDescendantCount(descendantCount);
        }
        return organizeModel;
    }

    public boolean isActive(OrganizeEntity organizeEntity) {
        var organizeIdList = new ArrayList<String>();
        while (true) {
            if (organizeEntity == null) {
                break;
            }
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            if (!organizeEntity.getIsActive()) {
                return false;
            }
            organizeIdList.add(organizeEntity.getId());
            organizeEntity = organizeEntity.getParent();
        }
        return true;
    }

    public long getLevel(OrganizeEntity organizeEntity) {
        var level = Math.max(getAncestorIdList(organizeEntity).size() - 1, 0);
        return level;
    }

    public List<String> getAncestorIdList(OrganizeEntity organizeEntity) {
        var organizeIdList = new ArrayList<String>();
        while (true) {
            if (organizeEntity == null) {
                break;
            }
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            organizeIdList.add(organizeEntity.getId());
            organizeEntity = organizeEntity.getParent();
        }
        return Lists.reverse(organizeIdList);
    }

}
