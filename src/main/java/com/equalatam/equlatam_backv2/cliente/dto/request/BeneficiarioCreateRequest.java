package com.equalatam.equlatam_backv2.cliente.dto.request;

import com.equalatam.equlatam_backv2.cliente.Enums;

public record BeneficiarioCreateRequest(
        String identificacion,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        Enums.TipoBeneficiario tipo
) {}
