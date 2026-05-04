package com.equalatam.equlatam_backv2.pedidos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ComprobanteRequest(
        @NotBlank String comprobanteBase64,  // imagen en base64
        String bancoOrigen,                  // puede actualizarse aquí si no vino en el paso 1
        String numeroReferencia
) {}