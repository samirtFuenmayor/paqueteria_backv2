package com.equalatam.equlatam_backv2.pedidos.dto.response;


import com.equalatam.equlatam_backv2.pedidos.entity.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        String numeroPedido,
        TipoPedido tipo,
        EstadoPedido estado,

        // Cliente
        UUID clienteId,
        String clienteNombres,
        String clienteApellidos,
        String clienteCasillero,
        String clienteIdentificacion,

        // Tracking externo
        String trackingExterno,
        String proveedor,
        String urlTracking,

        // Contenido
        String descripcion,
        Double peso,
        Double largo,
        Double ancho,
        Double alto,
        Double valorDeclarado,
        Integer cantidadItems,

        // Sucursales
        UUID sucursalOrigenId,
        String sucursalOrigenNombre,
        String sucursalOrigenPais,
        UUID sucursalDestinoId,
        String sucursalDestinoNombre,
        String sucursalDestinoCiudad,

        // Empleado que registró
        String registradoPor,

        // Fechas
        LocalDateTime fechaRegistro,
        LocalDateTime fechaRecepcionSede,
        LocalDateTime fechaSalidaExterior,
        LocalDateTime fechaLlegadaEcuador,
        LocalDateTime fechaDisponible,
        LocalDateTime fechaEntrega,

        String observaciones,
        String notasInternas,
        String fotoUrl,

        CategoriaPedido categoriaPedido,
        Boolean esPorTitular,
        java.util.List<PedidoItemResponse> items,
        Double pesoTotal,
        String tipoTarifa,

        FormaPago formaPago,
        EstadoPago estadoPago,
        String bancoOrigen,
        String numeroReferencia,
        boolean tieneComprobante,   // true/false sin exponer el base64 en listados
        LocalDateTime fechaSubidaComprobante,
        LocalDateTime fechaVerificacionPago,
        String motivoRechazo,

        // Facturación
        String factRazonSocial,
        String factRucCedula,
        String factEmail,
        String factDireccion,
        boolean factUsarDatosCliente,

        // ─── Datos del registro presencial ───────────────────────────────────────────
        boolean registradoEnSucursal,
        String sucursalAtencionNombre,   // nombre de la sucursal donde se atendió
        String registradoPorNombre   // nombre completo del agente/admin
) {
    public static PedidoResponse from(Pedido p) {
        var fact = p.getDatosFacturacion();
        return new PedidoResponse(
                p.getId(),
                p.getNumeroPedido(),
                p.getTipo(),
                p.getEstado(),

                p.getCliente().getId(),
                p.getCliente().getNombres(),
                p.getCliente().getApellidos(),
                p.getCliente().getCasillero(),
                p.getCliente().getNumeroIdentificacion(),

                p.getTrackingExterno(),
                p.getProveedor(),
                p.getUrlTracking(),

                p.getDescripcion(),
                p.getPeso(),
                p.getLargo(),
                p.getAncho(),
                p.getAlto(),
                p.getValorDeclarado(),
                p.getCantidadItems(),

                p.getSucursalOrigen() != null ? p.getSucursalOrigen().getId() : null,
                p.getSucursalOrigen() != null ? p.getSucursalOrigen().getNombre() : null,
                p.getSucursalOrigen() != null ? p.getSucursalOrigen().getPais() : null,
                p.getSucursalDestino() != null ? p.getSucursalDestino().getId() : null,
                p.getSucursalDestino() != null ? p.getSucursalDestino().getNombre() : null,
                p.getSucursalDestino() != null ? p.getSucursalDestino().getCiudad() : null,

                p.getRegistradoPor() != null ?
                        p.getRegistradoPor().getNombre() + " " + p.getRegistradoPor().getApellido() : null,

                p.getFechaRegistro(),
                p.getFechaRecepcionSede(),
                p.getFechaSalidaExterior(),
                p.getFechaLlegadaEcuador(),
                p.getFechaDisponible(),
                p.getFechaEntrega(),

                p.getObservaciones(),
                p.getNotasInternas(),
                p.getFotoUrl(),
                p.getCategoria(),
                p.getEsPorTitular(),
                p.getItems() != null ? p.getItems().stream().map(PedidoItemResponse::from).collect(java.util.stream.Collectors.toList())
                        : java.util.List.of(),
                p.getPesoTotal(),
                p.getTipoTarifa(),

                p.getFormaPago(),
                p.getEstadoPago(),
                p.getBancoOrigen(),
                p.getNumeroReferencia(),
                p.getComprobanteBase64() != null,
                p.getFechaSubidaComprobante(),
                p.getFechaVerificacionPago(),
                p.getMotivoRechazo(),

                fact != null ? fact.getRazonSocial() : null,
                fact != null ? fact.getRucCedula() : null,
                fact != null ? fact.getEmailFacturacion() : null,
                fact != null ? fact.getDireccionFacturacion() : null,
                fact != null && fact.getUsarDatosCliente() != null
                        ? fact.getUsarDatosCliente()
                        : false,
                p.isRegistradoEnSucursal(),

                p.getSucursalAtencion() != null
                        ? p.getSucursalAtencion().getNombre()
                        : null,

                p.getRegistradoPor() != null
                        ? p.getRegistradoPor().getNombre() + " " + p.getRegistradoPor().getApellido()
                        : null
        );
    }


}
