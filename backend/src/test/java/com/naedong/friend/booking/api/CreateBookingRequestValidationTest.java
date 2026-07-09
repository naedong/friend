package com.naedong.friend.booking.api;

import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static com.naedong.friend.testsupport.TestFixtures.MEETING_SPOT_ID;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Duration;
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
                futureStartTime(),
                futureEndTime()
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
                futureEndTime(),
                futureStartTime()
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
                futureStartTime(),
                futureEndTime()
        );
    }

    private Instant futureStartTime() {
        return Instant.now().plus(Duration.ofDays(7));
    }

    private Instant futureEndTime() {
        return Instant.now().plus(Duration.ofDays(7)).plus(Duration.ofHours(1));
    }
}
