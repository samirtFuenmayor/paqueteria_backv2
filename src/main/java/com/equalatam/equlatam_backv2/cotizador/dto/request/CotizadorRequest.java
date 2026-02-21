package com.equalatam.equlatam_backv2.cotizador.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CotizadorRequest(

        // Desde qué sucursal sale el paquete
        @NotNull(message = "La sucursal origen es obligatoria")
        UUID sucursalOrigenId,

        // Hacia qué sucursal va en Ecuador
        @NotNull(message = "La sucursal destino es obligatoria")
        UUID sucursalDestinoId,

        // Dimensiones y peso
        @NotNull(message = "El peso es obligatorio")
        @Positive(message = "El peso debe ser mayor a 0")
        Double peso,

        Double largo,
        Double ancho,
        Double alto,

        // Valor declarado para calcular seguro
        Double valorDeclarado,

        Integer cantidadPiezas
) {}
