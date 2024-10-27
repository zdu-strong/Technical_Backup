package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class SuperAdminOrganizeRoleQueryController extends BaseController {

    @GetMapping("/super_admin/organize_role/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestParam Long pageNum, @RequestParam Long pageSize,
            @RequestParam String organizeId) {
        this.permissionUtil.checkIsSignIn(request);
        // this.permissionUtil.checkAnyRole(request, SystemRoleEnum.SUPER_ADMIN);

        var paginationModel = this.superAdminOrganizeRoleQueryService.searchByPagination(pageNum, pageSize, organizeId);

        return ResponseEntity.ok(paginationModel);
    }

}
