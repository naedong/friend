package com.naedong.friend.gateway;

import java.util.UUID;

public interface PaymentGateway {

    void freezePayout(UUID bookingId, String reason);
}
