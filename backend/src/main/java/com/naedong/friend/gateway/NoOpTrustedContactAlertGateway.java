package com.naedong.friend.gateway;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class NoOpTrustedContactAlertGateway implements TrustedContactAlertGateway {

    @Override
    public void alertTrustedContacts(UUID bookingId, UUID userId, String message) {
        // Local development only. No real SMS or email is sent.
    }
}
