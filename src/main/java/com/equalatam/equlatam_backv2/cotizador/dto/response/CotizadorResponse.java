package com.equalatam.equlatam_backv2.cotizador.dto.response;

public record CotizadorResponse(

        // ─── Sucursales ───────────────────────────────────────────────────────
        String sucursalOrigen,
        String sucursalOrigenPais,
        String sucursalDestino,
        String sucursalDestinoCiudad,

        // ─── Pesos calculados ─────────────────────────────────────────────────
        Double pesoReal,            // Libras ingresadas
        Double pesoVolumetrico,     // (L x A x H) / 166
        Double pesoCobrable,        // El mayor de los dos
        String criterioPeso,        // "REAL" o "VOLUMETRICO"

        // ─── Desglose de costos ───────────────────────────────────────────────
        Double tarifaPorLibra,
        Double costoFlete,          // pesoCobrable × tarifaPorLibra
        Double costoManejo,         // Cargo fijo
        Double costoSeguro,         // % del valor declarado
        Double subtotal,
        Double costoTotal,          // Total a pagar

        // ─── Info adicional ───────────────────────────────────────────────────
        String moneda,
        String nota
) {}
