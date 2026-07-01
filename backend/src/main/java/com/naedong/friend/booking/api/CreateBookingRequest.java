package com.naedong.friend.booking.api;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID companionId,
        @NotBlank String category,
        @NotNull UUID meetingSpotId,
        @NotNull @Future Instant startTime,
        @NotNull @Future Instant endTime
) {
}
