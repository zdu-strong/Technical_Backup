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

    public void checkExistOrganize(String id) {
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

    public void checkOrganizeCanBeMove(String id, String parentId) {
        if (this.organizeService.isChildOfOrganize(id, parentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize cannot be moved");
        }
    }

    public void checkExistOrganizeWithIdCanBeBlank(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        this.checkExistOrganize(id);
    }

}
