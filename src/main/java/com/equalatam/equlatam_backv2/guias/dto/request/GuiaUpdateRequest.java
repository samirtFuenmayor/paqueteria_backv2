package com.equalatam.equlatam_backv2.guias.dto.request;

public record GuiaUpdateRequest(
        String descripcion,
        Double peso,
        Double valorDeclarado
) {}