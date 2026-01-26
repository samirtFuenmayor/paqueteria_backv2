package com.equalatam.equlatam_backv2.rutas.entity.dto.request;

import com.equalatam.equlatam_backv2.rutas.Enums;

public record RutaCreateRequest(
        String origen,
        String destino,
        Enums.TipoRuta tipo,
        String descripcion
) {}