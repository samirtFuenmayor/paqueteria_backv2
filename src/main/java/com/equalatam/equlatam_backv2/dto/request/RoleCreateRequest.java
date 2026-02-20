package com.equalatam.equlatam_backv2.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateRequest(
        @NotBlank(message = "El nombre del rol es obligatorio")
        String name
) {}