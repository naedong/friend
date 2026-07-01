package com.naedong.friend.safety.repository;

import com.naedong.friend.safety.domain.BookingSafetyEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSafetyEventRepository extends JpaRepository<BookingSafetyEvent, UUID> {
}
