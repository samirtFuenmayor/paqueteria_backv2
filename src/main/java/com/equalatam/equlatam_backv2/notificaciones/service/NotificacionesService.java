//package com.equalatam.equlatam_backv2.notificaciones.service;
//
//
//import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
//import com.equalatam.equlatam_backv2.notificaciones.dto.response.NotificacionesResponse;
//import com.equalatam.equlatam_backv2.notificaciones.entity.Notificaciones;
//import com.equalatam.equlatam_backv2.notificaciones.entity.TipoNotificaciones;
//import com.equalatam.equlatam_backv2.notificaciones.repository.NotificacionesRepository;
//import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
//import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class NotificacionesService {
//
//    private final NotificacionesRepository notificacionRepository;
//    private final JavaMailSender mailSender;
//    private final EmailTemplateService templateService;
//
//    @Value("${app.mail.from:noreply@equalatam.com}")
//    private String mailFrom;
//
//    @Value("${app.mail.from-name:Equalatam Paquetería}")
//    private String mailFromName;
//
//    // ─── Notificar cambio de estado (llamado automáticamente desde PedidoService)
//    @Async
//    public void notificarCambioEstado(Pedido pedido, EstadoPedido estado, String observacion) {
//        TipoNotificaciones tipo = obtenerTipo(estado);
//        String asunto = obtenerAsunto(estado, pedido.getNumeroPedido());
//        String html;
//
//        // Usar template especial para disponible y registrado
//        if (estado == EstadoPedido.DISPONIBLE_EN_SUCURSAL) {
//            html = templateService.templateDisponible(pedido);
//        } else if (estado == EstadoPedido.REGISTRADO) {
//            html = templateService.templatePedidoRegistrado(pedido);
//        } else {
//            html = templateService.templateCambioEstado(pedido, estado, observacion);
//        }
//
//        enviarYRegistrar(pedido.getCliente(), pedido, tipo, asunto, html);
//    }
//
//    // ─── Notificar registro de pedido ─────────────────────────────────────────
//    @Async
//    public void notificarPedidoRegistrado(Pedido pedido) {
//        String asunto = "✅ Pedido " + pedido.getNumeroPedido() + " registrado - Equalatam";
//        String html = templateService.templatePedidoRegistrado(pedido);
//        enviarYRegistrar(pedido.getCliente(), pedido,
//                TipoNotificaciones.PEDIDO_REGISTRADO, asunto, html);
//    }
//
//    // ─── Notificación manual (empleado envía mensaje personalizado) ───────────
//    public NotificacionesResponse enviarManual(UUID clienteId, UUID pedidoId,
//                                             String asunto, String mensaje,
//                                             Cliente cliente, Pedido pedido) {
//        String html = templateService.templateCambioEstado(pedido, pedido.getEstado(), mensaje);
//        return enviarYRegistrar(cliente, pedido,
//                TipoNotificaciones.NOTIFICACION_MANUAL, asunto, html);
//    }
//
//    // ─── Historial de notificaciones de un cliente ────────────────────────────
//    public List<NotificacionesResponse> findByCliente(UUID clienteId) {
//        return notificacionRepository.findByClienteIdOrderByCreadoEnDesc(clienteId)
//                .stream().map(NotificacionesResponse::from).collect(Collectors.toList());
//    }
//
//    // ─── Historial de notificaciones de un pedido ─────────────────────────────
//    public List<NotificacionesResponse> findByPedido(UUID pedidoId) {
//        return notificacionRepository.findByPedidoIdOrderByCreadoEnDesc(pedidoId)
//                .stream().map(NotificacionesResponse::from).collect(Collectors.toList());
//    }
//
//    // ─── Reenviar notificación fallida ────────────────────────────────────────
//    public NotificacionesResponse reenviar(UUID notificacionId) {
//        Notificaciones n = notificacionRepository.findById(notificacionId)
//                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
//
//        boolean enviado = enviarEmail(n.getEmailDestino(), n.getAsunto(), n.getMensaje());
//        n.setEnviado(enviado);
//        n.setEnviadoEn(enviado ? LocalDateTime.now() : null);
//        n.setErrorEnvio(enviado ? null : "Reenvío fallido");
//
//        return NotificacionesResponse.from(notificacionRepository.save(n));
//    }
//
//    // ─── Core: enviar email y registrar en BD ─────────────────────────────────
//    private NotificacionesResponse enviarYRegistrar(Cliente cliente, Pedido pedido,
//                                                  TipoNotificaciones tipo,
//                                                  String asunto, String html) {
//        Notificaciones n = new Notificaciones();
//        n.setCliente(cliente);
//        n.setPedido(pedido);
//        n.setTipo(tipo);
//        n.setAsunto(asunto);
//        n.setMensaje(html);
//        n.setEmailDestino(cliente.getEmail());
//
//        boolean enviado = enviarEmail(cliente.getEmail(), asunto, html);
//        n.setEnviado(enviado);
//        if (enviado) {
//            n.setEnviadoEn(LocalDateTime.now());
//        } else {
//            n.setErrorEnvio("Error al enviar email");
//        }
//
//        return NotificacionesResponse.from(notificacionRepository.save(n));
//    }
//
//    // ─── Envío real del email ──────────────────────────────────────────────────
//    private boolean enviarEmail(String destino, String asunto, String html) {
//        try {
//            MimeMessage mensaje = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
//            helper.setFrom(mailFrom, mailFromName);
//            helper.setTo(destino);
//            helper.setSubject(asunto);
//            helper.setText(html, true);
//            mailSender.send(mensaje);
//            log.info("✅ Email enviado a: {}", destino);
//            return true;
//        } catch (Exception e) {
//            log.error("❌ Error enviando email a {}: {}", destino, e.getMessage());
//            return false;
//        }
//    }
//
//    // ─── Helpers ──────────────────────────────────────────────────────────────
//    private TipoNotificaciones obtenerTipo(EstadoPedido estado) {
//        return switch (estado) {
//            case REGISTRADO             -> TipoNotificaciones.PEDIDO_REGISTRADO;
//            case RECIBIDO_EN_SEDE       -> TipoNotificaciones.PAQUETE_RECIBIDO_SEDE;
//            case EN_TRANSITO            -> TipoNotificaciones.PAQUETE_EN_TRANSITO;
//            case EN_ADUANA,
//                 RETENIDO_ADUANA        -> TipoNotificaciones.PAQUETE_EN_ADUANA;
//            case DISPONIBLE_EN_SUCURSAL -> TipoNotificaciones.PAQUETE_DISPONIBLE;
//            case ENTREGADO              -> TipoNotificaciones.PAQUETE_ENTREGADO;
//            default                     -> TipoNotificaciones.NOTIFICACION_MANUAL;
//        };
//    }
//
//    private String obtenerAsunto(EstadoPedido estado, String numeroPedido) {
//        String base = "Pedido " + numeroPedido;
//        return switch (estado) {
//            case RECIBIDO_EN_SEDE       -> "📦 " + base + " - Recibido en sede";
//            case EN_TRANSITO            -> "✈️ " + base + " - En tránsito a Ecuador";
//            case EN_ADUANA              -> "🔍 " + base + " - En revisión de aduana";
//            case RETENIDO_ADUANA        -> "⚠️ " + base + " - Retenido en aduana";
//            case LIBERADO_ADUANA        -> "✅ " + base + " - Liberado de aduana";
//            case RECIBIDO_EN_MATRIZ     -> "🏢 " + base + " - Llegó a Quito";
//            case EN_DISTRIBUCION        -> "🚚 " + base + " - En camino a tu sucursal";
//            case DISPONIBLE_EN_SUCURSAL -> "🎁 " + base + " - ¡Listo para retiro!";
//            case ENTREGADO              -> "✅ " + base + " - Entregado exitosamente";
//            case DEVUELTO               -> "↩️ " + base + " - Devuelto";
//            default                     -> "📋 " + base + " - Actualización de estado";
//        };
//    }
//}
