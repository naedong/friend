package com.naedong.friend.security.prod;

import com.naedong.friend.common.PolicyViolationException;
import com.naedong.friend.security.ActorProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev & !test")
public class JwtActorProvider implements ActorProvider {

    @Override
    public UUID currentActorId(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication) || !authentication.isAuthenticated()) {
            throw new PolicyViolationException("Authenticated JWT principal is required.");
        }

        String subject = jwtAuthentication.getToken().getSubject();
        try {
            return UUID.fromString(subject);
        } catch (RuntimeException exception) {
            throw new PolicyViolationException("Authenticated JWT subject must be a user UUID.");
        }
    }
}
