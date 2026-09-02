package com.mmendoza.registerofusers.dto.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
