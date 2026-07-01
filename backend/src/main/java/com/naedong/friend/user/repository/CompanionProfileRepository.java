package com.naedong.friend.user.repository;

import com.naedong.friend.user.domain.CompanionProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanionProfileRepository extends JpaRepository<CompanionProfile, UUID> {

    Optional<CompanionProfile> findByUserId(UUID userId);
}
