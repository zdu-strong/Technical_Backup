package com.springboot.project.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.project.common.baseController.BaseController;

@RestController
public class FriendshipController extends BaseController {

    @PostMapping("/friendship/add_to_friend_list")
    public ResponseEntity<?> addToFriendList(@RequestParam String friendId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var friendshipModel = this.friendshipService.addToFriendList(userId, friendId);
        return ResponseEntity.ok(friendshipModel);
    }

    @PostMapping("/friendship/add_to_blacklist")
    public ResponseEntity<?> addToBlacklist(@RequestParam String friendId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var friendshipModel = this.friendshipService.addToBlacklist(userId, friendId);
        return ResponseEntity.ok(friendshipModel);
    }

    @DeleteMapping("/friendship/delete_from_friend_list")
    public ResponseEntity<?> deleteFromFriendList(@RequestParam String friendId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        this.friendshipService.delete(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/friendship/delete_from_black_list")
    public ResponseEntity<?> deleteFromBlacklist(@RequestParam String friendId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        this.friendshipService.delete(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friendship/get_friend_list")
    public ResponseEntity<?> getFriendList(@RequestParam Long pageNum, @RequestParam Long pageSize) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getFriendList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/friendship/get_stranger_list")
    public ResponseEntity<?> getStrangerList(@RequestParam Long pageNum, @RequestParam Long pageSize)
            throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getStrangerList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/friendship/get_blacklist")
    public ResponseEntity<?> getBlacklist(@RequestParam Long pageNum, @RequestParam Long pageSize) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var pagination = this.friendshipService.getBlackList(pageNum, pageSize, userId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/friendship/get_friendship")
    public ResponseEntity<?> getFriendship(@RequestParam String friendId) throws IOException {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);

        var friendshipModel = this.friendshipService.getFriendship(userId, friendId);
        return ResponseEntity.ok(friendshipModel);
    }

}
