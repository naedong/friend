package com.naedong.friend.booking.api;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record LocationRequest(
        @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude
) {
    @AssertTrue(message = "latitude and longitude must be provided together")
    public boolean isCoordinatePairComplete() {
        return (latitude == null && longitude == null) || (latitude != null && longitude != null);
    }
}
