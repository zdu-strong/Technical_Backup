package com.springboot.project.service;

import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.UserModel;

@Service
public class SuperAdminUserQueryService extends BaseService {

    public PaginationModel<UserModel> searchByPagination(long pageNum, long pageSize) {
        var stream = this.UserEntity()
                .where(s -> s.getIsActive())
                .sortedDescendingBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.userFormatter.format(s));
    }

}
