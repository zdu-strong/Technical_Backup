package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.model.UserMessageModel;

@RestController
public class UserMessageController extends BaseController {

    @PostMapping("/user_message/send")
    public ResponseEntity<?> sendMessage(@RequestBody UserMessageModel userMessageModel) throws IOException {
        var user = this.userService.createUserIfNotExist(userMessageModel.getUser().getEmail());
        var userMessage = this.userMessageService.sendMessage(user.getId(), userMessageModel.getContent());
        return ResponseEntity.ok(userMessage);
    }

    @PostMapping("/user_message/recall")
    public ResponseEntity<?> recallMessage(@RequestParam String id) throws IOException {
        this.userMessageService.recallMessage(id);
        return ResponseEntity.ok().build();
    }

}
