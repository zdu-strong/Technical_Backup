package com.springboot.project.service;

import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.SystemRoleModel;

@Service
public class SuperAdminOrganizeRoleQueryService extends BaseService {

    public PaginationModel<SystemRoleModel> searchByPagination(long pageNum, long pageSize, String organizeId) {
        var stream = this.SystemRoleEntity()
                .joinList(s -> s.getOrganize().getAncestorList())
                .where(s -> s.getTwo().getAncestor().getId().equals(organizeId))
                .where(s -> s.getOne().getOrganize().getIsActive())
                .select(s -> s.getOne());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.systemRoleFormatter.format(s));
    }

}
