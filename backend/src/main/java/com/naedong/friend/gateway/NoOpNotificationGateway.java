package com.naedong.friend.gateway;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class NoOpNotificationGateway implements NotificationGateway {

    @Override
    public void notifyModerator(UUID bookingId, String message) {
        // Local development only. Production must connect a reviewed notification provider.
    }
}
