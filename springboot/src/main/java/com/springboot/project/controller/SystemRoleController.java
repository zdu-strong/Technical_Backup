package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.SystemRoleModel;

@RestController
public class SystemRoleController extends BaseController {

    @PostMapping("/system_role/create")
    public ResponseEntity<?> create(@RequestBody SystemRoleModel systemRole) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyRole(request, systemRole.getOrganize().getId(), SystemRoleEnum.ORGANIZE_ADMIN);
        this.organizeCheckService.checkNotBlankOrganizeId(systemRole.getOrganize().getId());
        this.organizeCheckService.checkOrganizeNoParent(systemRole.getOrganize().getId());
        this.systemRoleCheckService.checkSystemRoleNameCannotBeBlank(systemRole.getName());

        var systemRoleOne = this.systemRoleService.create(systemRole.getName(),
                systemRole.getSystemDefaultRoleList().stream().map(s -> s.getId()).toList(),
                systemRole.getOrganize().getId());

        return ResponseEntity.ok(systemRoleOne);
    }

}
