package com.equalatam.equlatam_backv2.tarifas.dto.request;

import com.equalatam.equlatam_backv2.tarifas.entity.TipoTarifa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record TarifaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "El tipo de tarifa es obligatorio")
        TipoTarifa tipo,

        UUID sucursalOrigenId,

        Double valorFijo,
        Double valorPorcentaje,
        Double valorMinimo,
        Double valorMaximo,
        Double pesoMinimo,
        Double pesoMaximo,

        LocalDateTime vigenciaDesde,
        LocalDateTime vigenciaHasta
) {}

