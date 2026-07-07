package com.naedong.friend.booking.api;

import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.MEETING_SPOT_ID;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CreateBookingRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validBookingPayloadIsAccepted() {
        assertThat(validator.validate(validRequest())).isEmpty();
    }

    @Test
    void categoryHasBoundedLength() {
        CreateBookingRequest request = new CreateBookingRequest(
                COMPANION_ID,
                "A".repeat(65),
                MEETING_SPOT_ID,
                Instant.parse("2026-07-08T10:00:00Z"),
                Instant.parse("2026-07-08T11:00:00Z")
        );

        assertThat(validator.validate(request))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("category"));
    }

    @Test
    void endTimeMustBeAfterStartTime() {
        CreateBookingRequest request = new CreateBookingRequest(
                COMPANION_ID,
                "COFFEE_BUDDY",
                MEETING_SPOT_ID,
                Instant.parse("2026-07-08T11:00:00Z"),
                Instant.parse("2026-07-08T10:00:00Z")
        );

        assertThat(validator.validate(request))
                .anyMatch(violation -> violation.getMessage().contains("after startTime"));
    }

    @Test
    void requiredFieldsRemainRequired() {
        CreateBookingRequest request = new CreateBookingRequest(
                null,
                "",
                null,
                null,
                null
        );

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("companionId", "category", "meetingSpotId", "startTime", "endTime");
    }

    private CreateBookingRequest validRequest() {
        return new CreateBookingRequest(
                COMPANION_ID,
                "COFFEE_BUDDY",
                MEETING_SPOT_ID,
                Instant.parse("2026-07-08T10:00:00Z"),
                Instant.parse("2026-07-08T11:00:00Z")
        );
    }
}
