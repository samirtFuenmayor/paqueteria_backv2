package com.equalatam.equlatam_backv2.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PermissionCreateRequest(
        @NotBlank(message = "El nombre del permiso es obligatorio")
        String name
) {}

