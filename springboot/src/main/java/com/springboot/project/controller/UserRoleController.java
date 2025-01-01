package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enumerate.SystemPermissionEnum;
import com.springboot.project.model.RoleModel;

@RestController
public class UserRoleController extends BaseController {

    @PostMapping("/user_role/create")
    public ResponseEntity<?> create(@RequestBody RoleModel userRoleModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyPermission(request, userRoleModel.getOrganize().getId(), SystemPermissionEnum.ORGANIZE_MANAGE_PERMISSION);
        this.organizeService.checkCannotBeEmptyById(userRoleModel.getOrganize().getId());
        this.organizeService.checkCannotHasParentOrganizeById(userRoleModel.getOrganize().getId());
        this.roleService.checkCannotBeEmptyOfName(userRoleModel);

        var userRoleModelOne = this.roleService.create(userRoleModel.getName(),
                userRoleModel.getPermissionList().stream()
                        .map(s -> SystemPermissionEnum.valueOf(this.permissionService.getById(s.getId()).getName())).toList(),
                userRoleModel.getOrganize().getId());

        return ResponseEntity.ok(userRoleModelOne);
    }

}
