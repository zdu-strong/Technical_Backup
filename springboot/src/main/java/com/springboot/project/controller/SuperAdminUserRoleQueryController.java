package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enumerate.SystemPermissionEnum;

@RestController
public class SuperAdminUserRoleQueryController extends BaseController {

    @GetMapping("/super_admin/user_role/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN_PERMISSION);

        var paginationModel = this.roleService.searchUserRoleForSuperAdminByPagination(pageNum, pageSize);

        return ResponseEntity.ok(paginationModel);
    }

}
