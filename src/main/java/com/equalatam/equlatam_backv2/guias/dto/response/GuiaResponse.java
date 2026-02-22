package com.equalatam.equlatam_backv2.guias.dto.response;


import com.equalatam.equlatam_backv2.guias.entity.EstadoGuia;
import com.equalatam.equlatam_backv2.guias.entity.Guia;

import java.time.LocalDateTime;
import java.util.UUID;

public record GuiaResponse(
        UUID id,
        String numeroGuia,
        EstadoGuia estado,

        // Pedido
        UUID pedidoId,
        String numeroPedido,
        String trackingExterno,

        // Remitente
        String remitenteNombre,
        String remitenteDireccion,
        String remitenteTelefono,
        String remitenteEmail,
        String remitentePais,

        // Destinatario
        UUID destinatarioId,
        String destinatarioNombre,
        String destinatarioCasillero,
        String destinatarioTelefono,

        // Ruta
        String sucursalOrigenNombre,
        String sucursalOrigenPais,
        String sucursalDestinoNombre,
        String sucursalDestinoCiudad,

        // Contenido
        String descripcionContenido,
        Double pesoDeclarado,
        Double pesoVolumetrico,
        Double pesoCobrable,
        Double valorDeclarado,
        Integer cantidadPiezas,
        Double largo,
        Double ancho,
        Double alto,

        // Transporte
        String numeroDespacho,
        String aerolinea,
        String numeroVuelo,
        String guiaAerea,

        // Costos
        Double tarifaPorLibra,
        Double costoFlete,
        Double costoManejo,
        Double costoSeguro,
        Double costoTotal,

        // Auditoría
        String generadaPor,
        LocalDateTime fechaGeneracion,
        LocalDateTime fechaEntrega,
        String observaciones
) {
    public static GuiaResponse from(Guia g) {
        return new GuiaResponse(
                g.getId(),
                g.getNumeroGuia(),
                g.getEstado(),

                g.getPedido().getId(),
                g.getPedido().getNumeroPedido(),
                g.getPedido().getTrackingExterno(),

                g.getRemitenteNombre(),
                g.getRemitenteDireccion(),
                g.getRemitenteTelefono(),
                g.getRemitenteEmail(),
                g.getRemitentePais(),

                g.getDestinatario().getId(),
                g.getDestinatario().getNombres() + " " + g.getDestinatario().getApellidos(),
                g.getDestinatario().getCasillero(),
                g.getDestinatario().getTelefono(),

                g.getSucursalOrigen() != null ? g.getSucursalOrigen().getNombre() : null,
                g.getSucursalOrigen() != null ? g.getSucursalOrigen().getPais() : null,
                g.getSucursalDestino() != null ? g.getSucursalDestino().getNombre() : null,
                g.getSucursalDestino() != null ? g.getSucursalDestino().getCiudad() : null,

                g.getDescripcionContenido(),
                g.getPesoDeclarado(),
                g.getPesoVolumetrico(),
                g.getPesoCobrable(),
                g.getValorDeclarado(),
                g.getCantidadPiezas(),
                g.getLargo(),
                g.getAncho(),
                g.getAlto(),

                g.getNumeroDespacho(),
                g.getAerolinea(),
                g.getNumeroVuelo(),
                g.getGuiaAerea(),

                g.getTarifaPorLibra(),
                g.getCostoFlete(),
                g.getCostoManejo(),
                g.getCostoSeguro(),
                g.getCostoTotal(),

                g.getGeneradaPor() != null ?
                        g.getGeneradaPor().getNombre() + " " + g.getGeneradaPor().getApellido() : "Sistema",
                g.getFechaGeneracion(),
                g.getFechaEntrega(),
                g.getObservaciones()
        );
    }
}