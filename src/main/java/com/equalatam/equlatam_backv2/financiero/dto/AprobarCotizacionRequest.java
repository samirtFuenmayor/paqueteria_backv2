package com.equalatam.equlatam_backv2.financiero.dto;

import com.equalatam.equlatam_backv2.financiero.enums.FormaPago;

public record AprobarCotizacionRequest(
        FormaPago formaPago,          // TRANSFERENCIA, DEPOSITO, EFECTIVO
        String referenciaPago,         // Número de comprobante
        String observaciones
) {}