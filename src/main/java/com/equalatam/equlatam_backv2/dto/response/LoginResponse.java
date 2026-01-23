package com.equalatam.equlatam_backv2.dto.response;

import java.util.Set;

public record LoginResponse(
        String username,
        Set<String> roles,
        Set<String> permissions,
        String token
) {
}