package com.naedong.friend.booking.repository;

import com.naedong.friend.booking.domain.SafeMeetingSpot;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafeMeetingSpotRepository extends JpaRepository<SafeMeetingSpot, UUID> {
}
