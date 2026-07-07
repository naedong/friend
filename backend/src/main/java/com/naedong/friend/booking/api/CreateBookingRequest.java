package com.naedong.friend.booking.api;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID companionId,
        @NotBlank @Size(max = 64) String category,
        @NotNull UUID meetingSpotId,
        @NotNull @Future Instant startTime,
        @NotNull @Future Instant endTime
) {
    @AssertTrue(message = "endTime must be after startTime")
    public boolean isTimeRangeOrdered() {
        return startTime == null || endTime == null || endTime.isAfter(startTime);
    }
}
