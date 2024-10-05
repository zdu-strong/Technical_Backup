package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class SuperAdminQuerySystemRoleController extends BaseController {

    @GetMapping("/super_admin/system_role/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        this.permissionUtil.checkIsSignIn(request);
        // this.permissionUtil.checkAnyRole(request, SystemRoleEnum.SUPER_ADMIN);

        var paginationModel = this.superAdminQuerySystemRoleService.searchByPagination(pageNum, pageSize);

        return ResponseEntity.ok(paginationModel);
    }

}
