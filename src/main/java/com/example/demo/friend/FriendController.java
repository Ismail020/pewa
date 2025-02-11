package com.example.demo.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @PostMapping("/add")
    public void addFriend(@RequestBody FriendRequest friendRequest) {
        friendService.addFriend(friendRequest.getUsername(), friendRequest.getFriendUsername());
    }

    @GetMapping("/{username}")
    public List<Friend> getFriends(@PathVariable String username) {
        return friendService.getFriends(username);
    }
}

class FriendRequest {
    private String username;
    private String friendUsername;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }
}
