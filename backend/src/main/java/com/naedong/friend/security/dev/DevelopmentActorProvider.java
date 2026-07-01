package com.naedong.friend.security.dev;

import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.security.ActorProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
@ConditionalOnProperty(name = "friend.security.dev-actor-enabled", havingValue = "true")
public class DevelopmentActorProvider implements ActorProvider {

    public static final String ACTOR_HEADER = "X-Dev-Actor-Id";

    @Override
    public UUID currentActorId(HttpServletRequest request) {
        // TODO SECURITY: Replace this header with the authenticated subject from Spring Security.
        String rawActorId = request.getHeader(ACTOR_HEADER);
        if (rawActorId == null || rawActorId.isBlank()) {
            throw new PolicyViolationException("Development actor header is required: " + ACTOR_HEADER);
        }
        return UUID.fromString(rawActorId);
    }
}
