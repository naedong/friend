package com.naedong.friend.gateway;

import java.util.UUID;

public interface KycProviderGateway {

    String createVerificationSession(UUID userId);
}
