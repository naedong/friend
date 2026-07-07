package com.naedong.friend.security.prod;

import com.naedong.friend.security.ActorProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public class FailClosedProductionActorProvider implements ActorProvider {

    public static final String MESSAGE = "No production ActorProvider is configured. "
            + "Do not start Friend outside dev/test without real authentication.";

    @Override
    public UUID currentActorId(HttpServletRequest request) {
        throw new IllegalStateException(MESSAGE);
    }
}
