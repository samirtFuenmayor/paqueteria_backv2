package com.equalatam.equlatam_backv2.financiero.dto;

import com.equalatam.equlatam_backv2.pedidos.dto.request.DatosFacturacionRequest;
import com.equalatam.equlatam_backv2.pedidos.entity.FormaPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AprobarCotizacionRequest(
        @NotNull FormaPago formaPago,
        String bancoOrigen,           // solo si TRANSFERENCIA
        String referenciaPago,        // número de transferencia o recibo
        @NotNull @Valid DatosFacturacionRequest datosFacturacion,
        String observaciones
) {}