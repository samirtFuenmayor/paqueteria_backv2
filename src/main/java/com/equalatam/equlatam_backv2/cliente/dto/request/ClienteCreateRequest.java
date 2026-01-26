package com.equalatam.equlatam_backv2.cliente.dto.request;

import java.time.LocalDate;

public record ClienteCreateRequest(
        String identificacion,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        String callePrincipal,
        String calleSecundaria,
        String nacionalidad,
        String provincia,
        String ciudad,
        LocalDate fechaNacimiento
) {}
