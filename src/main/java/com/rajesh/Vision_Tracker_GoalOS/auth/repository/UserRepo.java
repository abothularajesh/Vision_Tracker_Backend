package com.rajesh.Vision_Tracker_GoalOS.auth.repository;

import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByusername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByProviderAndProviderId(
            String provider,
            String providerId
    );
}
