package com.naedong.friend.gateway;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class NoOpKycProviderGateway implements KycProviderGateway {

    @Override
    public String createVerificationSession(UUID userId) {
        return "noop-kyc-session-" + userId;
    }
}
