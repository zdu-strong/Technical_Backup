package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.model.OrganizeModel;

@RestController
public class OrganizeController extends BaseController {

    @PostMapping("/organize/create")
    public ResponseEntity<?> create(@RequestBody OrganizeModel organizeModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.organizeCheckService.checkExistParentOrganizeForCreateOrganize(organizeModel);

        var organize = this.organizeService.create(organizeModel);
        this.organizeUtil.refresh(organize.getId());
        return ResponseEntity.ok(organize);
    }

    @PutMapping("/organize/update")
    public ResponseEntity<?> update(@RequestBody OrganizeModel organizeModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.organizeCheckService.checkNotBlankOrganizeId(organizeModel.getId());
        this.organizeCheckService.checkExistOrganize(organizeModel.getId());

        this.organizeService.update(organizeModel);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/organize/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.organizeCheckService.checkNotBlankOrganizeId(id);
        this.organizeCheckService.checkExistOrganize(id);

        this.organizeService.delete(id);
        this.organizeUtil.refresh(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organize/move")
    public ResponseEntity<?> move(@RequestParam String id, @RequestParam(required = false) String parentId) {
        this.permissionUtil.checkIsSignIn(request);
        this.organizeCheckService.checkNotBlankOrganizeId(id);
        this.organizeCheckService.checkExistOrganize(id);
        this.organizeCheckService.checkExistOrganize(parentId);
        this.organizeCheckService.checkOrganizeCanBeMove(id, parentId);

        this.organizeUtil.move(id, parentId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/organize")
    public ResponseEntity<?> getOrganizeById(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.organizeCheckService.checkNotBlankOrganizeId(id);
        this.organizeCheckService.checkExistOrganize(id);

        var organizeModel = this.organizeService.getById(id);
        return ResponseEntity.ok(organizeModel);
    }

}
