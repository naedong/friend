package com.naedong.friend.gateway;

import java.util.UUID;

public interface NotificationGateway {

    void notifyModerator(UUID bookingId, String message);
}
