package com.naedong.friend.booking.service;

import com.naedong.friend.common.PolicyViolationException;

public class InvalidBookingStateTransitionException extends PolicyViolationException {

    public InvalidBookingStateTransitionException(String message) {
        super(message);
    }
}
