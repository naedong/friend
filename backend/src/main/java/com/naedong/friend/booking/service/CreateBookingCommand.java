package com.naedong.friend.booking.service;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingCommand(
        UUID customerId,
        UUID companionId,
        String categoryName,
        UUID meetingSpotId,
        Instant startTime,
        Instant endTime
) {
}
