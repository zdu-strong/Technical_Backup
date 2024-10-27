package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class SuperAdminUserQueryController extends BaseController {

    @GetMapping("/super_admin/user/search/pagination")
    public ResponseEntity<?> getUserById(@RequestParam Long pageNum, @RequestParam Long pageSize) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var pagination = this.superAdminUserQueryService.searchByPagination(pageNum, pageSize);
        return ResponseEntity.ok(pagination);
    }

}