package com.naedong.friend.booking.api;

import com.naedong.friend.booking.domain.Booking;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID customerId,
        UUID companionId,
        String category,
        UUID meetingSpotId,
        Instant startTime,
        Instant endTime,
        String status
) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomerId(),
                booking.getCompanionId(),
                booking.getCategory().name(),
                booking.getMeetingSpotId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus().name()
        );
    }
}
