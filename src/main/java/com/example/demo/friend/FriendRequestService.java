package com.example.demo.friend;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendRequestService {

    /**
     * Service to handle friend requests.
     *
     * Author: Danann & Shahin
     */
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method to send a friend request.
     *
     * @param senderEmail   the email of the sender
     * @param receiverEmail the email of the receiver
     */
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

    /**
     * Method to accept a friend request.
     *
     * @param requestId the ID of the friend request
     */
    public void acceptFriendRequest(Integer requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(friendRequest);
    }

    /**
     * Method to reject a friend request.
     *
     * @param requestId the ID of the friend request
     */
    public void rejectFriendRequest(Integer requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        friendRequest.setStatus(FriendRequest.Status.REJECTED);
        friendRequestRepository.save(friendRequest);
    }

    /**
     * Method to get pending friend requests for a user.
     *
     * @param userEmail the email of the user
     * @return a list of pending friend requests
     */
    public List<FriendRequest> getPendingRequestsForUser(String userEmail) {
        return friendRequestRepository.findByReceiver_EmailAndStatus(userEmail, FriendRequest.Status.PENDING);
    }
}
