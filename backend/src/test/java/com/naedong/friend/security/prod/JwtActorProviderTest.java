package com.naedong.friend.security.prod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.naedong.friend.common.PolicyViolationException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class JwtActorProviderTest {

    private final JwtActorProvider actorProvider = new JwtActorProvider();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void usesAuthenticatedJwtSubjectAsActorId() {
        UUID actorId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(authenticatedJwt(actorId.toString()));

        assertThat(actorProvider.currentActorId(null)).isEqualTo(actorId);
    }

    @Test
    void rejectsMissingJwtAuthentication() {
        assertThatThrownBy(() -> actorProvider.currentActorId(null))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("Authenticated JWT principal");
    }

    @Test
    void rejectsNonUuidJwtSubject() {
        SecurityContextHolder.getContext().setAuthentication(authenticatedJwt("not-a-user-id"));

        assertThatThrownBy(() -> actorProvider.currentActorId(null))
                .isInstanceOf(PolicyViolationException.class)
                .hasMessageContaining("subject must be a user UUID");
    }

    private Jwt jwtWithSubject(String subject) {
        return new Jwt(
                "token-value",
                Instant.parse("2026-07-07T00:00:00Z"),
                Instant.parse("2026-07-07T01:00:00Z"),
                Map.of("alg", "none"),
                Map.of("sub", subject)
        );
    }

    private JwtAuthenticationToken authenticatedJwt(String subject) {
        return new JwtAuthenticationToken(jwtWithSubject(subject), List.of());
    }
}
