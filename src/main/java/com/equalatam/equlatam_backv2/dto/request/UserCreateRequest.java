package com.equalatam.equlatam_backv2.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public record UserCreateRequest(
        String nombre,
        String apellido,
        String username,
        String correo,
        String telefono,
        String nacionalidad,
        String provincia,
        String ciudad,
        String direccion,
        LocalDate fechaNacimiento,
        String password,
        UUID sucursalId
) {}
