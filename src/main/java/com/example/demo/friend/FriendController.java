package com.example.demo.friend;

import com.example.demo.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add")
    public ResponseEntity<User> addFriend(@RequestBody FriendRequest request) {
        return ResponseEntity.ok(friendService.addFriend(request.getUserId(), request.getFriendId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<User>> getFriends(@PathVariable Integer userId) {
        return ResponseEntity.ok(friendService.getFriends(userId));
    }
}
