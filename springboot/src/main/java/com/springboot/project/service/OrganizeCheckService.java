package com.springboot.project.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseService.BaseService;

@Service
public class OrganizeCheckService extends BaseService {

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

    public void checkOrganizeCanBeMove(String id, String parentId) {
        if (this.organizeService.isChildOfOrganize(id, parentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize cannot be moved");
        }
    }

    public void checkExistOrganizeWithIdCanBeBlank(String id){
        if(StringUtils.isBlank(id)){
            return;
        }
        this.checkExistOrganize(id);
    }

}
