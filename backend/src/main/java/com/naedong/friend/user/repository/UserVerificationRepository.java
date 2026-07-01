package com.naedong.friend.user.repository;

import com.naedong.friend.user.domain.UserVerification;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {

    Optional<UserVerification> findByUserId(UUID userId);
}
