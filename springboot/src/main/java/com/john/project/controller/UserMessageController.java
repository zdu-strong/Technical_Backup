package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.UserMessageModel;

@RestController
public class UserMessageController extends BaseController {

    @PostMapping("/user-message/send")
    public ResponseEntity<?> sendMessage(@RequestBody UserMessageModel userMessageModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfUserMessageContent(userMessageModel);

        var userMessage = this.userMessageService.sendMessage(userMessageModel, request);
        return ResponseEntity.ok(userMessage);
    }

    @PostMapping("/user-message/recall")
    public ResponseEntity<?> recallMessage(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.userMessageService.checkExistsUserMessage(id);
        this.userMessageService.checkCanRecallUserMessage(id, request);

        this.userMessageService.recallMessage(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user-message/delete")
    public ResponseEntity<?> deleteMessage(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.userMessageService.checkExistsUserMessage(id);
        this.userMessageService.checkCanDeleteUserMessage(id, request);

        this.userMessageService.deleteMessage(id, request);
        return ResponseEntity.ok().build();
    }

}
