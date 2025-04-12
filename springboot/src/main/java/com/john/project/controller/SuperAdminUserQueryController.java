package com.john.project.controller;

import com.john.project.model.SuperAdminUserQueryPaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.enums.SystemPermissionEnum;

@RestController
public class SuperAdminUserQueryController extends BaseController {

    @GetMapping("/super-admin/user/search/pagination")
    public ResponseEntity<?> searchByPagination(SuperAdminUserQueryPaginationModel superAdminUserQueryPaginationModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);

        var pagination = this.userService.searchForSuperAdminByPagination(superAdminUserQueryPaginationModel);
        return ResponseEntity.ok(pagination);
    }

}
