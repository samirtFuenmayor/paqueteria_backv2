package com.equalatam.equlatam_backv2.tarifas.dto.response;


import com.equalatam.equlatam_backv2.tarifas.entity.Tarifa;
import com.equalatam.equlatam_backv2.tarifas.entity.TipoTarifa;

import java.time.LocalDateTime;
import java.util.UUID;

public record TarifaResponse(
        UUID id,
        String nombre,
        String descripcion,
        TipoTarifa tipo,
        UUID sucursalOrigenId,
        String sucursalOrigenNombre,
        String sucursalOrigenPais,
        Double valorFijo,
        Double valorPorcentaje,
        Double valorMinimo,
        Double valorMaximo,
        Double pesoMinimo,
        Double pesoMaximo,
        LocalDateTime vigenciaDesde,
        LocalDateTime vigenciaHasta,
        boolean activa,
        LocalDateTime creadoEn
) {
    public static TarifaResponse from(Tarifa t) {
        return new TarifaResponse(
                t.getId(),
                t.getNombre(),
                t.getDescripcion(),
                t.getTipo(),
                t.getSucursalOrigen() != null ? t.getSucursalOrigen().getId() : null,
                t.getSucursalOrigen() != null ? t.getSucursalOrigen().getNombre() : null,
                t.getSucursalOrigen() != null ? t.getSucursalOrigen().getPais() : null,
                t.getValorFijo(),
                t.getValorPorcentaje(),
                t.getValorMinimo(),
                t.getValorMaximo(),
                t.getPesoMinimo(),
                t.getPesoMaximo(),
                t.getVigenciaDesde(),
                t.getVigenciaHasta(),
                t.isActiva(),
                t.getCreadoEn()
        );
    }
}