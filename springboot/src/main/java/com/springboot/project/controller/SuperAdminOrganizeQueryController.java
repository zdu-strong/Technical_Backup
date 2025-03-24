package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enums.SystemPermissionEnum;

@RestController
public class SuperAdminOrganizeQueryController extends BaseController {

    @GetMapping("/super-admin/organize/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);

        var paginationModel = this.organizeService.searchOrganizeForSuperAdminByPagination(pageNum, pageSize);

        return ResponseEntity.ok(paginationModel);
    }

}
