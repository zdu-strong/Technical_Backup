package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enums.SystemPermissionEnum;

@RestController
public class SuperAdminOrganizeRoleQueryController extends BaseController {

    @GetMapping("/super-admin/organize-role/search/pagination")
    public ResponseEntity<?> searchByPagination(@RequestParam Long pageNum, @RequestParam Long pageSize,
            @RequestParam String organizeId) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, SystemPermissionEnum.SUPER_ADMIN);

        var paginationModel = this.roleOrganizeRelationService.searchOrganizeRoleForSuperAdminByPagination(pageNum, pageSize, organizeId, true);

        return ResponseEntity.ok(paginationModel);
    }

}
