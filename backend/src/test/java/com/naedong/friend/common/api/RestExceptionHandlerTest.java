package com.naedong.friend.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;

class RestExceptionHandlerTest {

    private static final String BAD_REQUEST_MESSAGE = "Request validation failed.";

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void badRequestResponseDoesNotExposeRawExceptionMessage() {
        ResponseEntity<ErrorResponse> response = handler.badRequest(
                new IllegalArgumentException("raw payload contained private note")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().message()).isEqualTo(BAD_REQUEST_MESSAGE);
        assertThat(response.getBody().message()).doesNotContain("private note");
    }

    @Test
    void unreadableRequestBodyUsesCommonBadRequestEnvelope() {
        ResponseEntity<ErrorResponse> response = handler.badRequest(
                new HttpMessageNotReadableException(
                        "malformed body with raw content",
                        new MockHttpInputMessage(new byte[0])
                )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().message()).isEqualTo(BAD_REQUEST_MESSAGE);
    }

    @Test
    void constraintViolationUsesCommonBadRequestEnvelope() {
        ResponseEntity<ErrorResponse> response = handler.badRequest(
                new ConstraintViolationException("email=private@example.com", Set.of())
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().message()).isEqualTo(BAD_REQUEST_MESSAGE);
        assertThat(response.getBody().message()).doesNotContain("private@example.com");
    }
}
