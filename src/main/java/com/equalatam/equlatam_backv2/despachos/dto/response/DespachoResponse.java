package com.equalatam.equlatam_backv2.despachos.dto.response;

import com.equalatam.equlatam_backv2.despachos.entity.Despacho;
import com.equalatam.equlatam_backv2.despachos.entity.DespachoDetalle;
import com.equalatam.equlatam_backv2.despachos.entity.EstadoDespacho;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record DespachoResponse(
        UUID id,
        String numeroDespacho,
        EstadoDespacho estado,

        // Ruta
        UUID sucursalOrigenId,
        String sucursalOrigenNombre,
        String sucursalOrigenPais,
        UUID sucursalDestinoId,
        String sucursalDestinoNombre,
        String sucursalDestinoPais,

        // Transporte
        String aerolinea,
        String numeroVuelo,
        String guiaAerea,
        String numeroContenedor,
        String tipoTransporte,

        // Fechas
        LocalDateTime fechaCreacion,
        LocalDateTime fechaSalidaProgramada,
        LocalDateTime fechaSalidaReal,
        LocalDateTime fechaLlegadaProgramada,
        LocalDateTime fechaLlegadaReal,

        // Totales
        Integer totalPedidos,
        Double pesoTotal,
        Double valorTotalDeclarado,

        // Pedidos incluidos
        List<DetallePedidoResponse> pedidos,

        String creadoPor,
        String observaciones
) {
    public static DespachoResponse from(Despacho d) {
        List<DetallePedidoResponse> pedidos = d.getDetalles() != null
                ? d.getDetalles().stream().map(DetallePedidoResponse::from).collect(Collectors.toList())
                : List.of();

        return new DespachoResponse(
                d.getId(),
                d.getNumeroDespacho(),
                d.getEstado(),

                d.getSucursalOrigen().getId(),
                d.getSucursalOrigen().getNombre(),
                d.getSucursalOrigen().getPais(),
                d.getSucursalDestino().getId(),
                d.getSucursalDestino().getNombre(),
                d.getSucursalDestino().getPais(),

                d.getAerolinea(),
                d.getNumeroVuelo(),
                d.getGuiaAerea(),
                d.getNumeroContenedor(),
                d.getTipoTransporte(),

                d.getFechaCreacion(),
                d.getFechaSalidaProgramada(),
                d.getFechaSalidaReal(),
                d.getFechaLlegadaProgramada(),
                d.getFechaLlegadaReal(),

                d.getTotalPedidos(),
                d.getPesoTotal(),
                d.getValorTotalDeclarado(),

                pedidos,
                d.getCreadoPor() != null ?
                        d.getCreadoPor().getNombre() + " " + d.getCreadoPor().getApellido() : null,
                d.getObservaciones()
        );
    }

    // ─── DTO anidado para cada pedido del despacho ────────────────────────────
    public record DetallePedidoResponse(
            UUID detalleId,
            UUID pedidoId,
            String numeroPedido,
            String clienteNombre,
            String clienteCasillero,
            String trackingExterno,
            String descripcion,
            Double peso,
            Double valorDeclarado,
            String sucursalDestinoPedido,
            String estadoPedido,
            LocalDateTime agregadoEn
    ) {
        public static DetallePedidoResponse from(DespachoDetalle dd) {
            return new DetallePedidoResponse(
                    dd.getId(),
                    dd.getPedido().getId(),
                    dd.getPedido().getNumeroPedido(),
                    dd.getPedido().getCliente().getNombres() + " " + dd.getPedido().getCliente().getApellidos(),
                    dd.getPedido().getCliente().getCasillero(),
                    dd.getPedido().getTrackingExterno(),
                    dd.getPedido().getDescripcion(),
                    dd.getPedido().getPeso(),
                    dd.getPedido().getValorDeclarado(),
                    dd.getPedido().getSucursalDestino() != null ?
                            dd.getPedido().getSucursalDestino().getNombre() : null,
                    dd.getPedido().getEstado().name(),
                    dd.getAgregadoEn()
            );
        }
    }
}
