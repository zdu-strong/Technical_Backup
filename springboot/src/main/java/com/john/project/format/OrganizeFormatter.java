package com.john.project.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.john.project.entity.OrganizeEntity;
import com.john.project.entity.OrganizeRelationEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.john.project.common.baseService.BaseService;
import com.john.project.entity.*;
import com.john.project.model.OrganizeModel;

@Service
public class OrganizeFormatter extends BaseService {

    public OrganizeModel format(OrganizeEntity organizeEntity) {
        var organizeModel = new OrganizeModel();
        BeanUtils.copyProperties(organizeEntity, organizeModel);
        organizeModel.setChildCount(0L)
                .setDescendantCount(0L)
                .setChildList(Lists.newArrayList())
                .setParent(new OrganizeModel().setId(
                        Optional.ofNullable(
                                organizeEntity.getParent())
                                .map(OrganizeEntity::getId)
                                .filter(s -> StringUtils.isNotBlank(s))
                                .orElse(StringUtils.EMPTY)))
                .setLevel(this.getLevel(organizeEntity));

        var id = organizeEntity.getId();

        if (this.isActive(organizeEntity)) {
            var childOrganizeCount = this.streamAll(OrganizeEntity.class)
                    .where(s -> s.getParent().getId().equals(id))
                    .where(s -> !s.getIsDeleted())
                    .count();
            organizeModel.setChildCount(childOrganizeCount);
            var descendantCount = Math.max(this.streamAll(OrganizeRelationEntity.class)
                    .where(s -> s.getAncestor().getId().equals(id))
                    .count(), 1) - 1;
            organizeModel.setDescendantCount(descendantCount);
        }
        return organizeModel;
    }

    public boolean isActive(OrganizeEntity organizeEntity) {
        var organizeIdList = new ArrayList<String>();
        while (true) {
            if (organizeEntity.getIsDeleted()) {
                return false;
            }
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            organizeIdList.add(organizeEntity.getId());
            if (organizeEntity.getIsCompany()) {
                break;
            }
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
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            organizeIdList.add(organizeEntity.getId());
            if (organizeEntity.getIsCompany()) {
                break;
            }
            organizeEntity = organizeEntity.getParent();
        }
        return Lists.reverse(organizeIdList);
    }

}
