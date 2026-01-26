package com.equalatam.equlatam_backv2.rutas.entity.dto.request;

import com.equalatam.equlatam_backv2.rutas.Enums;

public record RutaEstadoRequest(
        Enums.EstadoRuta estado
) {}