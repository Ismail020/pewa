package com.example.demo.friend;

import com.example.demo.friend.FriendRequest.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

    /**
     * Repository for friend requests.
     *
     * Author: Danann & Shahin
     */
    List<FriendRequest> findByReceiver_EmailAndStatus(String receiverEmail, Status status);

    List<FriendRequest> findBySender_EmailAndStatus(String senderEmail, Status status);
}
