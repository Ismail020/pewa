package com.example.demo.friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.user.User;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
    @Query("SELECT f FROM Friend f WHERE f.user = :user")
    List<Friend> findAllByUser(@Param("user") User user);

    boolean existsByUserAndFriend(User user, User friend);
}
