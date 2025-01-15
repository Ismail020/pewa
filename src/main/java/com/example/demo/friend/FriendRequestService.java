package com.example.demo.friend;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    // Method to send a friend request
    public void sendFriendRequest(String senderEmail, String receiverEmail) {
        User sender = userRepository.findUserByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findUserByEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequest.Status.PENDING)
                .build();

        friendRequestRepository.save(friendRequest);
    }

    // Method to accept a friend request
    public void acceptFriendRequest(Integer requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(friendRequest);
    }

    // Method to reject a friend request
    public void rejectFriendRequest(Integer requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        friendRequest.setStatus(FriendRequest.Status.REJECTED);
        friendRequestRepository.save(friendRequest);
    }

    // Method to get pending friend requests for a specific user
    public List<FriendRequest> getPendingRequestsForUser(String userEmail) {
        return friendRequestRepository.findByReceiver_EmailAndStatus(userEmail, FriendRequest.Status.PENDING);
    }
}
