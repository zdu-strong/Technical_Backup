package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.enumerate.SystemRoleEnum;
import com.springboot.project.model.UserRoleModel;

@RestController
public class UserRoleController extends BaseController {

    @PostMapping("/user_role/create")
    public ResponseEntity<?> create(@RequestBody UserRoleModel userRoleModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.permissionUtil.checkAnyRole(request, userRoleModel.getOrganize().getId(), SystemRoleEnum.ORGANIZE_ADMIN);
        this.organizeService.checkCannotBeEmptyById(userRoleModel.getOrganize().getId());
        this.organizeService.checkCannotHasParentOrganizeById(userRoleModel.getOrganize().getId());
        this.userRoleService.checkCannotBeEmptyOfName(userRoleModel);

        var userRoleModelOne = this.userRoleService.create(userRoleModel.getName(),
                userRoleModel.getSystemRoleList().stream()
                        .map(s -> SystemRoleEnum.valueOfRole(this.systemRoleService.getById(s.getId()).getName())).toList(),
                userRoleModel.getOrganize().getId());

        return ResponseEntity.ok(userRoleModelOne);
    }

}
