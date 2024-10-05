package com.springboot.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.PaginationModel;

@Service
public class OrganizeService extends BaseService {

    public OrganizeModel create(OrganizeModel organizeModel) {
        var parentOrganize = this.getParentOrganize(organizeModel);
        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(newId());
        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setIsActive(true);
        organizeEntity
                .setDeactiveKey("");
        organizeEntity.setCreateDate(new Date());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setParent(parentOrganize);
        this.persist(organizeEntity);

        if (organizeEntity.getParent() == null) {
            this.systemRoleService.refreshForCompany(organizeEntity.getId());
        }

        return this.organizeFormatter.format(organizeEntity);
    }

    public void update(OrganizeModel organizeModel) {
        var id = organizeModel.getId();
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();

        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

    public void delete(String id) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setIsActive(false);
        organizeEntity.setDeactiveKey(Generators.timeBasedReorderedGenerator().generate().toString());
        this.merge(organizeEntity);
    }

    public OrganizeModel getById(String id) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        return this.organizeFormatter.format(organizeEntity);
    }

    public PaginationModel<OrganizeModel> searchByName(Long pageNum, Long pageSize, String name, String organizeId) {
        var stream = this.OrganizeRelationEntity()
                .where(s -> s.getAncestor().getId().equals(organizeId))
                .where(s -> s.getDescendant().getName().contains(name))
                .select(s -> s.getDescendant());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.organizeFormatter.format(s));
    }

    public void move(String id, String parentId) {
        var parentOrganizeEntity = this
                .getParentOrganize(new OrganizeModel().setParent(new OrganizeModel().setId(parentId)));
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        organizeEntity.setParent(parentOrganizeEntity);
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);

        if (organizeEntity.getParent() == null) {
            this.systemRoleService.refreshForCompany(organizeEntity.getId());
        }
    }

    public boolean isChildOfOrganize(String id, String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return false;
        }
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        var isChild = this.organizeFormatter.getAncestorIdList(organizeEntity).contains(parentId);
        return isChild;
    }

    public List<String> getAccestorIdList(String id) {
        var organizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.organizeFormatter.getAncestorIdList(organizeEntity);
    }

    public List<String> getChildIdList(String id) {
        var childIdList = this.OrganizeEntity().where(s -> s.getParent().getId().equals(id))
                .where(s -> s.getIsActive())
                .select(s -> s.getId())
                .toList();
        return childIdList;
    }

    public PaginationModel<OrganizeModel> getOrganizeByPagination(Long pageNum, Long pageSize) {
        var stream = this.OrganizeEntity()
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.organizeFormatter.format(s));
    }

    public OrganizeModel getTopOrganize(String organizeId) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue();
        var organizeIdList = new ArrayList<String>();
        while (true) {
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            if (organizeEntity.getParent() == null) {
                break;
            }
            organizeIdList.add(organizeEntity.getId());
            organizeEntity = organizeEntity.getParent();
        }
        return this.organizeFormatter.format(organizeEntity);
    }

    private OrganizeEntity getParentOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = organizeModel.getParent() == null ? null : organizeModel.getParent().getId();
        if (StringUtils.isBlank(parentOrganizeId)) {
            return null;
        }

        var parentOrganizeEntity = this.OrganizeEntity()
                .where(s -> s.getId().equals(parentOrganizeId))
                .getOnlyValue();
        return parentOrganizeEntity;
    }

}
