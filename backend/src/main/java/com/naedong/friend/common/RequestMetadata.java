package com.naedong.friend.common;

public record RequestMetadata(String ipAddress, String userAgent) {

    public static RequestMetadata empty() {
        return new RequestMetadata(null, null);
    }
}
