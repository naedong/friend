package com.naedong.friend.booking.repository;

import com.naedong.friend.booking.domain.Booking;
import com.naedong.friend.booking.domain.BookingStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByStatusInAndEndTimeBefore(Collection<BookingStatus> statuses, Instant endTime);

    Optional<Booking> findFirstByCustomerIdOrCompanionIdOrderByCreatedAtDesc(
            UUID customerId,
            UUID companionId
    );
}
