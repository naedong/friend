package com.naedong.friend.gateway;

import java.util.UUID;

public interface TrustedContactAlertGateway {

    void alertTrustedContacts(UUID bookingId, UUID userId, String message);
}
