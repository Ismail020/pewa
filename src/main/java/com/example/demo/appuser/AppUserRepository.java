package com.example.demo.appuser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    
    @Query("SELECT s FROM AppUser s WHERE s.email = ?1")
    Optional<AppUser> findUserByEmail(String email);
}
