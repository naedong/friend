package com.naedong.friend.security.dev;

import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.security.ActorProvider;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
@ConditionalOnProperty(name = "friend.security.dev-actor-enabled", havingValue = "true")
public class DevelopmentActorProvider implements ActorProvider {

    private static final Logger log = LoggerFactory.getLogger(DevelopmentActorProvider.class);

    public static final String ACTOR_HEADER = "X-Dev-Actor-Id";

    @PostConstruct
    void warnDevActorEnabled() {
        log.warn("Development actor header is ENABLED. Do not deploy with friend.security.dev-actor-enabled=true.");
    }

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
