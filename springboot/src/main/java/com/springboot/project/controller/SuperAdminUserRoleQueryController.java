package com.springboot.project.controller;

import com.springboot.project.model.SuperAdminUserRoleQueryPaginationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enums.SystemPermissionEnum;

@RestController
public class SuperAdminUserRoleQueryController extends BaseController {

    @GetMapping("/super-admin/user-role/search/pagination")
    public ResponseEntity<?> searchByPagination(SuperAdminUserRoleQueryPaginationModel superAdminUserRoleQueryPaginationModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);

        var paginationModel = this.userRoleRelationService.searchUserRoleForSuperAdminByPagination(superAdminUserRoleQueryPaginationModel);

        return ResponseEntity.ok(paginationModel);
    }

}
