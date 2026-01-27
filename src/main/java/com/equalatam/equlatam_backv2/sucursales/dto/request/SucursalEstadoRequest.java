package com.equalatam.equlatam_backv2.sucursales.dto.request;

import com.equalatam.equlatam_backv2.sucursales.Enums;

public record SucursalEstadoRequest(
        Enums.EstadoSucursal estado
) {}
