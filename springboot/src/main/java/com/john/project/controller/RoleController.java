package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.enums.SystemPermissionEnum;
import com.john.project.model.RoleModel;

@RestController
public class RoleController extends BaseController {

    @PostMapping("/role/create")
    public ResponseEntity<?> create(@RequestBody RoleModel roleModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.roleService.checkCannotBeEmptyOfName(roleModel);
        this.roleService.checkCannotBeEmptyOfPermissionList(roleModel);
        this.roleService.checkCanCreateUserRole(roleModel, request);
        this.roleService.checkCanCreateOrganizeRole(roleModel, request);

        var roleOneModel = this.roleService.create(
                roleModel.getName(),
                roleModel.getPermissionList()
                        .stream()
                        .map(s -> SystemPermissionEnum.parse(s))
                        .toList(),
                roleModel.getOrganizeList()
                        .stream()
                        .map(s -> s.getId())
                        .toList());

        return ResponseEntity.ok(roleOneModel);
    }

}
