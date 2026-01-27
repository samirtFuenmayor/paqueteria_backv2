package com.equalatam.equlatam_backv2.dto.request;

import java.util.UUID;

public record UserCreateRequest(
        String username,
        String password,
        UUID sucursalId
) {}
