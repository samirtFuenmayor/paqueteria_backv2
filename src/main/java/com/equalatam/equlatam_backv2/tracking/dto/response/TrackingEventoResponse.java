package com.equalatam.equlatam_backv2.tracking.dto.response;

import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.tracking.entity.TrackingEvento;

import java.time.LocalDateTime;
import java.util.UUID;

public record TrackingEventoResponse(
        UUID id,
        EstadoPedido estado,
        String descripcion,
        String sucursalNombre,
        String sucursalPais,
        String ubicacionDetalle,
        String registradoPor,
        boolean visibleParaCliente,
        String notaInterna,         // null para clientes
        String numeroDespacho,
        LocalDateTime fechaEvento
) {
    // ─── Vista completa para empleados ────────────────────────────────────────
    public static TrackingEventoResponse from(TrackingEvento t) {
        return new TrackingEventoResponse(
                t.getId(),
                t.getEstado(),
                t.getDescripcion(),
                t.getSucursal() != null ? t.getSucursal().getNombre() : null,
                t.getSucursal() != null ? t.getSucursal().getPais() : null,
                t.getUbicacionDetalle(),
                t.getRegistradoPor() != null ?
                        t.getRegistradoPor().getNombre() + " " +
                                t.getRegistradoPor().getApellido() : "Sistema",
                t.isVisibleParaCliente(),
                t.getNotaInterna(),
                t.getNumeroDespacho(),
                t.getFechaEvento()
        );
    }

    // ─── Vista pública para clientes (sin notas internas) ────────────────────
    public static TrackingEventoResponse fromPublic(TrackingEvento t) {
        return new TrackingEventoResponse(
                t.getId(),
                t.getEstado(),
                t.getDescripcion(),
                t.getSucursal() != null ? t.getSucursal().getNombre() : null,
                t.getSucursal() != null ? t.getSucursal().getPais() : null,
                t.getUbicacionDetalle(),
                null,   // Ocultar quién registró
                true,
                null,   // Ocultar nota interna
                t.getNumeroDespacho(),
                t.getFechaEvento()
        );
    }
}
