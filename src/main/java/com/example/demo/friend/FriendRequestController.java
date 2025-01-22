package com.example.demo.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friend-requests")
public class FriendRequestController {

    /**
     * Controller to handle friend requests.
     *
     * Author: Danann & Shahin
     */
    @Autowired
    private FriendRequestService friendRequestService;

    @PostMapping("/send")
    public void sendFriendRequest(@RequestParam String senderEmail, @RequestParam String receiverEmail) {
        friendRequestService.sendFriendRequest(senderEmail, receiverEmail);
    }

    @PostMapping("/accept")
    public void acceptFriendRequest(@RequestParam Integer requestId) {
        friendRequestService.acceptFriendRequest(requestId);
    }

    @PostMapping("/reject")
    public void rejectFriendRequest(@RequestParam Integer requestId) {
        friendRequestService.rejectFriendRequest(requestId);
    }

    @GetMapping("/pending")
    public List<FriendRequest> getPendingRequests(@RequestParam String userEmail) {
        return friendRequestService.getPendingRequestsForUser(userEmail);
    }
}
