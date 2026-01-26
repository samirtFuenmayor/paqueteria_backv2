package com.equalatam.equlatam_backv2.guias.dto.request;

import java.util.UUID;

public record GuiaCreateRequest(
        UUID clienteId,
        UUID beneficiarioId,
        String descripcion,
        Double peso,
        Double valorDeclarado
) {}