package com.naedong.friend.booking.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LocationRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void locationMayBeOmitted() {
        assertThat(validator.validate(new LocationRequest(null, null))).isEmpty();
    }

    @Test
    void latitudeAndLongitudeMustBeProvidedTogether() {
        assertThat(validator.validate(new LocationRequest(new BigDecimal("37.566500"), null)))
                .anyMatch(violation -> violation.getMessage().contains("provided together"));

        assertThat(validator.validate(new LocationRequest(null, new BigDecimal("126.978000"))))
                .anyMatch(violation -> violation.getMessage().contains("provided together"));
    }

    @Test
    void completeLocationIsAccepted() {
        assertThat(validator.validate(new LocationRequest(
                new BigDecimal("37.566500"),
                new BigDecimal("126.978000")
        ))).isEmpty();
    }
}
