package com.springboot.project.controller;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;

@RestController
public class UserMessageController extends BaseController {

    @PostMapping("/user_message/send")
    public ResponseEntity<?> sendMessage(@RequestBody UserMessageModel userMessageModel) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        if (StringUtils.isBlank(userMessageModel.getUrl()) && StringUtils.isBlank(userMessageModel.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in the message content");
        }

        userMessageModel.setUser(new UserModel().setId(this.permissionUtil.getUserId(request)));
        var userMessage = this.userMessageService.sendMessage(userMessageModel);
        return ResponseEntity.ok(userMessage);
    }

    @PostMapping("/user_message/recall")
    public ResponseEntity<?> recallMessage(@RequestParam String id) throws IOException {
        this.permissionUtil.checkIsSignIn(request);
        this.userMessageCheckService.checkExistsUserMessage(id);
        this.userMessageCheckService.checkCanRecallUserMessage(id, request);

        this.userMessageService.recallMessage(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user_message/delete")
    public ResponseEntity<?> deleteMessage(@RequestParam String id) throws IOException {
        this.permissionUtil.checkIsSignIn(request);
        this.userMessageCheckService.checkExistsUserMessage(id);
        this.userMessageCheckService.checkCanDeleteUserMessage(id, request);

        this.userMessageService.deleteMessage(id, request);
        return ResponseEntity.ok().build();
    }

}
