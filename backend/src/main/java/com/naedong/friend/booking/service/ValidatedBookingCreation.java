package com.naedong.friend.booking.service;

import com.naedong.friend.booking.domain.CompanionCategory;
import java.time.Instant;
import java.util.UUID;

public record ValidatedBookingCreation(
        UUID customerId,
        UUID companionId,
        CompanionCategory category,
        UUID meetingSpotId,
        Instant startTime,
        Instant endTime
) {
}
