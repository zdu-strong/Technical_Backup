package com.john.project.common.DistributedExecution;

import com.john.project.common.baseDistributedExecution.BaseDistributedExecution;
import com.john.project.model.PaginationModel;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanDistributedExecution extends BaseDistributedExecution {

    @Override
    public PaginationModel<?> searchByPagination() {
        return this.tokenService.searchByPagination(1L, 1L);
    }

    @Override
    public void executeTask(long pageNum) {
        var paginationModel = this.tokenService.searchByPagination(pageNum, 1L);
        for (var tokenModel : paginationModel.getItems()) {
            if (this.tokenService.getIsInvalidOfTokenEntity(tokenModel.getId())) {
                this.tokenService.deleteTokenEntityWithHardDelete(tokenModel.getId());
            }
        }
    }

}
