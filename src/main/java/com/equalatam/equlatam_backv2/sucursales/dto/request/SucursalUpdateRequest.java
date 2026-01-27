package com.equalatam.equlatam_backv2.sucursales.dto.request;

public record SucursalUpdateRequest(
        String nombre,
        String direccion,
        String ciudad,
        String provincia,
        String telefono
) {}