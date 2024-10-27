package com.springboot.project.service;

import java.util.Arrays;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.SystemRoleModel;

@Service
public class SuperAdminUserRoleQueryService extends BaseService {

    public PaginationModel<SystemRoleModel> searchByPagination(long pageNum, long pageSize) {
        var roles = Arrays.stream(SystemRoleEnum.values())
                .filter(s -> !s.getIsOrganizeRole())
                .map(s -> s.getRole())
                .toList();
        var systemRoleList = this.SystemDefaultRoleEntity()
                .where(s -> roles.contains(s.getName()))
                .selectAllList(s -> s.getSystemRoleRelationList())
                .select(s -> s.getSystemRole())
                .where(s -> s.getIsActive())
                .where(s -> s.getOrganize() == null)
                .filter(s -> Arrays.stream(SystemRoleEnum.values())
                        .anyMatch(m -> m.getRole().equals(s.getName()) && !m.getIsOrganizeRole()))
                .toList();
        var stream = JinqStream.from(systemRoleList);
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.systemRoleFormatter.format(s));
    }

}
