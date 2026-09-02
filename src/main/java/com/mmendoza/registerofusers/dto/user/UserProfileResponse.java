package com.mmendoza.registerofusers.dto.user;

import java.util.Set;

public record UserProfileResponse(
        String username,
        Set<String> roles
) {
}
