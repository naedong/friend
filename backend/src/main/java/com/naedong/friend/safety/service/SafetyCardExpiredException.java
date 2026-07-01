package com.naedong.friend.safety.service;

import com.naedong.friend.common.PolicyViolationException;

public class SafetyCardExpiredException extends PolicyViolationException {

    public SafetyCardExpiredException(String message) {
        super(message);
    }
}
