package com.equalatam.equlatam_backv2.sucursales.dto.request;

public record SucursalCreateRequest(
        String nombre,
        String direccion,
        String ciudad,
        String provincia,
        String telefono
) {}