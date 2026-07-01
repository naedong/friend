package com.naedong.friend.safety.repository;

import com.naedong.friend.safety.domain.SafetyCard;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyCardRepository extends JpaRepository<SafetyCard, UUID> {

    Optional<SafetyCard> findByPublicToken(String publicToken);

    Optional<SafetyCard> findByBookingId(UUID bookingId);
}
