package com.naedong.friend.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface ActorProvider {

    UUID currentActorId(HttpServletRequest request);
}
