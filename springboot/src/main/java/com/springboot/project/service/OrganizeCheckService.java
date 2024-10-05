package com.springboot.project.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.model.OrganizeModel;

@Service
public class OrganizeCheckService extends BaseService {

    public void checkExistParentOrganizeForCreateOrganize(OrganizeModel organizeModel) {
        if (organizeModel.getParent() != null) {
            var parentOrganizeId = organizeModel.getParent().getId();
            if (StringUtils.isNotBlank(parentOrganizeId)) {
                this.checkExistOrganize(parentOrganizeId);
            }
        }
    }

    public void checkOrganizeCanBeMove(String id, String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return;
        }
        if (id.equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize cannot be moved");
        }
        if (this.organizeService.isChildOfOrganize(parentId, id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize cannot be moved");
        }
    }

    public void checkExistOrganize(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        var exists = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .filter(s -> this.organizeFormatter.isActive(s))
                .map(s -> this.organizeFormatter.format(s))
                .findFirst()
                .isPresent();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize does not exist");
        }
    }

    public void checkNotBlankOrganizeId(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrganizeId cannot be empty");
        }
    }

    public void checkOrganizeNoParent(String id) {
        var isPresent = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .map(s -> s.getParent())
                .findFirst()
                .isPresent();
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be a company");
        }
    }

}
