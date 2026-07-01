package com.naedong.friend.booking.repository;

import com.naedong.friend.booking.domain.BookingCheckin;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingCheckinRepository extends JpaRepository<BookingCheckin, UUID> {
}
