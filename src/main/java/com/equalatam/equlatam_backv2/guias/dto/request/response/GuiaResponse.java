package com.equalatam.equlatam_backv2.guias.dto.request.response;

import com.equalatam.equlatam_backv2.guias.Enums;

import java.util.UUID;

public record GuiaResponse(
        UUID id,
        String numeroGuia,
        Enums.EstadoGuia estado,
        String descripcion,
        Double peso,
        Double valorDeclarado
) {}