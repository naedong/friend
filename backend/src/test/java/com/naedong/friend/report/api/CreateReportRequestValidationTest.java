package com.naedong.friend.report.api;

import static com.naedong.friend.testsupport.TestFixtures.COMPANION_ID;
import static org.assertj.core.api.Assertions.assertThat;

import com.naedong.friend.report.domain.ReportReason;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class CreateReportRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validReportPayloadIsAccepted() {
        assertThat(validator.validate(new CreateReportRequest(
                COMPANION_ID,
                ReportReason.HARASSMENT,
                true
        ))).isEmpty();
    }

    @Test
    void reportedUserIsRequired() {
        assertThat(validator.validate(new CreateReportRequest(
                null,
                ReportReason.HARASSMENT,
                false
        )))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("reportedUserId"));
    }

    @Test
    void reasonIsRequired() {
        assertThat(validator.validate(new CreateReportRequest(
                COMPANION_ID,
                null,
                false
        )))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("reason"));
    }
}
