package com.equalatam.equlatam_backv2.tracking.dto.response;

import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TrackingResumenResponse(
        // ─── Info del pedido ──────────────────────────────────────────────────
        UUID pedidoId,
        String numeroPedido,
        EstadoPedido estadoActual,
        String descripcion,
        String trackingExterno,
        String proveedor,

        // ─── Cliente ──────────────────────────────────────────────────────────
        String clienteNombre,
        String clienteCasillero,

        // ─── Ruta ─────────────────────────────────────────────────────────────
        String sucursalOrigen,
        String sucursalDestino,

        // ─── Fechas clave ─────────────────────────────────────────────────────
        LocalDateTime fechaRegistro,
        LocalDateTime fechaRecepcionSede,
        LocalDateTime fechaSalidaExterior,
        LocalDateTime fechaLlegadaEcuador,
        LocalDateTime fechaDisponible,
        LocalDateTime fechaEntrega,

        // ─── Historial de eventos ─────────────────────────────────────────────
        List<TrackingEventoResponse> historial
) {
    public static TrackingResumenResponse from(Pedido p, List<TrackingEventoResponse> historial) {
        return new TrackingResumenResponse(
                p.getId(),
                p.getNumeroPedido(),
                p.getEstado(),
                p.getDescripcion(),
                p.getTrackingExterno(),
                p.getProveedor(),

                p.getCliente().getNombres() + " " + p.getCliente().getApellidos(),
                p.getCliente().getCasillero(),

                p.getSucursalOrigen() != null ? p.getSucursalOrigen().getNombre() : null,
                p.getSucursalDestino() != null ? p.getSucursalDestino().getNombre() : null,

                p.getFechaRegistro(),
                p.getFechaRecepcionSede(),
                p.getFechaSalidaExterior(),
                p.getFechaLlegadaEcuador(),
                p.getFechaDisponible(),
                p.getFechaEntrega(),

                historial
        );
    }
}