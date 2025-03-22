package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class FriendshipController extends BaseController {

    @PostMapping("/friendship/add-to-friend-list")
    public ResponseEntity<?> addToFriendList(@RequestParam String friendId) {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var friendshipModel = this.friendshipService.addToFriendList(userId, friendId);
        return ResponseEntity.ok(friendshipModel);
    }

    @PostMapping("/friendship/add-to-blacklist")
    public ResponseEntity<?> addToBlacklist(@RequestParam String friendId) {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var friendshipModel = this.friendshipService.addToBlacklist(userId, friendId);
        return ResponseEntity.ok(friendshipModel);
    }

    @DeleteMapping("/friendship/delete-from-friend-list")
    public ResponseEntity<?> deleteFromFriendList(@RequestParam String friendId) {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        this.friendshipService.delete(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/friendship/delete-from-black-list")
    public ResponseEntity<?> deleteFromBlacklist(@RequestParam String friendId) {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        this.friendshipService.delete(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friendship/get-friend-list")
    public ResponseEntity<?> getFriendList(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getFriendList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/friendship/get-stranger-list")
    public ResponseEntity<?> getStrangerList(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getStrangerList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/friendship/get-blacklist")
    public ResponseEntity<?> getBlacklist(@RequestParam Long pageNum, @RequestParam Long pageSize) {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getBlackList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/friendship/get-friendship")
    public ResponseEntity<?> getFriendship(@RequestParam String friendId) {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var friendshipModel = this.friendshipService.getFriendship(userId, friendId);
        return ResponseEntity.ok(friendshipModel);
    }

}
