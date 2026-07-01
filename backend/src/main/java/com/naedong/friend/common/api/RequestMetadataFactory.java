package com.naedong.friend.common.api;

import com.naedong.friend.common.RequestMetadata;
import jakarta.servlet.http.HttpServletRequest;

public final class RequestMetadataFactory {

    private RequestMetadataFactory() {
    }

    public static RequestMetadata from(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String ipAddress = forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.split(",")[0].trim();
        return new RequestMetadata(ipAddress, request.getHeader("User-Agent"));
    }
}
