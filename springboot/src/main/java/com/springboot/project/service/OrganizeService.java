package com.springboot.project.service;

import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.PaginationModel;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.OrganizeEntity;

@Service
public class OrganizeService extends BaseService {

    public OrganizeModel create(OrganizeModel organizeModel) {
        var parentOrganize = this.getParentOrganize(organizeModel);
        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(newId());
        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setIsDeleted(false);
        organizeEntity
                .setDeletedKey("");
        organizeEntity.setCreateDate(new Date());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setParent(parentOrganize);
        this.persist(organizeEntity);

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
        organizeEntity.setIsDeleted(true);
        organizeEntity.setDeletedKey(Generators.timeBasedReorderedGenerator().generate().toString());
        this.merge(organizeEntity);
    }

    public OrganizeModel getById(String id) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();

        return this.organizeFormatter.format(organizeEntity);
    }

    public void checkExistOrganize(String id) {
        var exists = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .map(s -> this.organizeFormatter.format(s))
                .filter(s -> !s.getIsDeleted())
                .findFirst()
                .isPresent();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize does not exist");
        }
    }

    public PaginationModel<OrganizeModel> searchByName(Long pageNum, Long pageSize, String name, String organizeId) {
        var stream = this.OrganizeClosureEntity()
                .where(s -> s.getAncestor().getId().equals(organizeId))
                .where(s -> !s.getIsDeleted())
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
    }

    public boolean isChildOfOrganize(String id, String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return false;
        }
        var isChild = false;
        var organizeIdList = new ArrayList<String>();
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id)).getOnlyValue();
        while (true) {
            if (organizeEntity == null) {
                break;
            }
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            if (organizeEntity.getId().equals(parentId)) {
                isChild = true;
                break;
            }
            organizeIdList.add(organizeEntity.getId());
            organizeEntity = organizeEntity.getParent();
        }
        return isChild;
    }

    public void checkOrganizeCanBeMove(String id, String parentId) {
        if (this.isChildOfOrganize(id, parentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize cannot be moved");
        }
    }

    public PaginationModel<OrganizeModel> getChildOrganizeListThatContainsDeleted(Long pageNum, Long pageSize,
            String organizeId) {
        var stream = this.OrganizeEntity().where(s -> s.getParent().getId().equals(organizeId))
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.organizeFormatter.format(s));
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
