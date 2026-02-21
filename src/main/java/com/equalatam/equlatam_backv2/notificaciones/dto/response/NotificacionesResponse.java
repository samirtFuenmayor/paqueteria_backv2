package com.equalatam.equlatam_backv2.notificaciones.dto.response;

import com.equalatam.equlatam_backv2.notificaciones.entity.Notificaciones;
import com.equalatam.equlatam_backv2.notificaciones.entity.TipoNotificaciones;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacionesResponse(
        UUID id,
        UUID clienteId,
        String clienteNombre,
        UUID pedidoId,
        String numeroPedido,
        TipoNotificaciones tipo,
        String asunto,
        String mensaje,
        String emailDestino,
        boolean enviado,
        String errorEnvio,
        LocalDateTime creadoEn,
        LocalDateTime enviadoEn
) {
    public static NotificacionesResponse from(Notificaciones n) {
        return new NotificacionesResponse(
                n.getId(),
                n.getCliente().getId(),
                n.getCliente().getNombres() + " " + n.getCliente().getApellidos(),
                n.getPedido() != null ? n.getPedido().getId() : null,
                n.getPedido() != null ? n.getPedido().getNumeroPedido() : null,
                n.getTipo(),
                n.getAsunto(),
                n.getMensaje(),
                n.getEmailDestino(),
                n.isEnviado(),
                n.getErrorEnvio(),
                n.getCreadoEn(),
                n.getEnviadoEn()
        );
    }
}