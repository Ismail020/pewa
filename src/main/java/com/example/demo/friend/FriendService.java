package com.example.demo.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;

    public void addFriend(String username, String friendUsername) {
        User user = userRepository.findUserByUsername(username).orElseThrow();
        User friend = userRepository.findUserByUsername(friendUsername).orElseThrow();
        Friend newFriend = new Friend();
        newFriend.setUser(user);
        newFriend.setFriend(friend);
        friendRepository.save(newFriend);
    }
}
