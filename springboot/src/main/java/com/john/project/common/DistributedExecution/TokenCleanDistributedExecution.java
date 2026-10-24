package com.john.project.common.DistributedExecution;

import com.john.project.common.baseDistributedExecution.BaseDistributedExecution;
import com.john.project.model.PaginationModel;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanDistributedExecution extends BaseDistributedExecution {

    @Override
    public PaginationModel<?> searchByPagination() {
        return this.tokenService.searchForInvalidTokenByPagination(1L, 1L);
    }

    @Override
    public void executeTask(long pageNum) {
        var paginationModel = this.tokenService.searchForInvalidTokenByPagination(pageNum,
                1L);
        for (var tokenModel : paginationModel.getItems()) {
            this.tokenService.deleteTokenEntityWithHardDelete(tokenModel.getId());
        }
    }

}
