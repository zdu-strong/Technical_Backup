package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class UserController extends BaseController {

    @GetMapping("/user")
    public ResponseEntity<?> getUserById(@RequestParam String id) throws IOException {
        this.permissionUtil.checkIsSignIn(request);
        this.userCheckService.checkExistUserById(id);

        var userModel = this.userService.getUser(id);
        return ResponseEntity.ok(userModel);
    }

}
