package com.equalatam.equlatam_backv2.notificaciones.service;

import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateService {

    // ─── Template base HTML ───────────────────────────────────────────────────
    private String base(String contenido) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <style>
                body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 30px auto; background: white;
                             border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                .header { background: #1a237e; color: white; padding: 30px; text-align: center; }
                .header h1 { margin: 0; font-size: 24px; }
                .header p { margin: 5px 0 0; opacity: 0.85; font-size: 14px; }
                .body { padding: 30px; color: #333; }
                .estado-badge { display: inline-block; background: #e8f5e9; color: #2e7d32;
                                padding: 8px 20px; border-radius: 20px; font-weight: bold;
                                font-size: 14px; margin: 15px 0; }
                .info-box { background: #f8f9fa; border-left: 4px solid #1a237e;
                            padding: 15px 20px; border-radius: 0 8px 8px 0; margin: 20px 0; }
                .info-box p { margin: 5px 0; font-size: 14px; }
                .info-box strong { color: #1a237e; }
                .btn { display: inline-block; background: #1a237e; color: white;
                       padding: 12px 30px; border-radius: 25px; text-decoration: none;
                       font-weight: bold; margin: 20px 0; }
                .footer { background: #f4f4f4; padding: 20px; text-align: center;
                          font-size: 12px; color: #999; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <h1>📦 Equalatam Paquetería</h1>
                  <p>Tu paquete, nuestra prioridad</p>
                </div>
                <div class="body">
                  %s
                </div>
                <div class="footer">
                  <p>© 2026 Equalatam. Todos los derechos reservados.</p>
                  <p>Si tienes dudas escríbenos a soporte@equalatam.com</p>
                </div>
              </div>
            </body>
            </html>
        """.formatted(contenido);
    }

    // ─── Template: cambio de estado ───────────────────────────────────────────
    public String templateCambioEstado(Pedido pedido, EstadoPedido estado, String observacion) {
        String icono = obtenerIcono(estado);
        String descripcion = obtenerDescripcion(estado);

        String contenido = """
            <h2>Hola, %s 👋</h2>
            <p>Te informamos que tu paquete ha actualizado su estado:</p>

            <div style="text-align: center;">
              <span class="estado-badge">%s %s</span>
            </div>

            <div class="info-box">
              <p><strong>Número de pedido:</strong> %s</p>
              <p><strong>Descripción:</strong> %s</p>
              <p><strong>Casillero:</strong> %s</p>
              %s
              %s
            </div>

            <p>%s</p>

            <p style="text-align:center;">
              <a class="btn" href="#">Ver estado en la app</a>
            </p>

            <p style="color:#999; font-size:13px;">
              Puedes rastrear tu paquete en cualquier momento desde nuestra app
              usando tu número de pedido <strong>%s</strong>.
            </p>
        """.formatted(
                pedido.getCliente().getNombres(),
                icono, estado.name().replace("_", " "),
                pedido.getNumeroPedido(),
                pedido.getDescripcion(),
                pedido.getCliente().getCasillero(),
                pedido.getTrackingExterno() != null ?
                        "<p><strong>Tracking externo:</strong> " + pedido.getTrackingExterno() + "</p>" : "",
                observacion != null && !observacion.isBlank() ?
                        "<p><strong>Nota:</strong> " + observacion + "</p>" : "",
                descripcion,
                pedido.getNumeroPedido()
        );

        return base(contenido);
    }

    // ─── Template: pedido registrado ─────────────────────────────────────────
    public String templatePedidoRegistrado(Pedido pedido) {
        String contenido = """
            <h2>¡Pedido registrado exitosamente! 🎉</h2>
            <p>Hola <strong>%s</strong>, tu pedido ha sido registrado en nuestro sistema.</p>

            <div class="info-box">
              <p><strong>Número de pedido:</strong> %s</p>
              <p><strong>Descripción:</strong> %s</p>
              <p><strong>Tu casillero:</strong> %s</p>
              <p><strong>Sede de recepción:</strong> %s</p>
              <p><strong>Sucursal de entrega:</strong> %s</p>
              %s
            </div>

            <p>Usa tu casillero <strong>%s</strong> como dirección de entrega en tus compras online.
            Nosotros recibiremos tu paquete y te avisaremos cuando llegue. 📬</p>

            <p style="text-align:center;">
              <a class="btn" href="#">Rastrear mi pedido</a>
            </p>
        """.formatted(
                pedido.getCliente().getNombres(),
                pedido.getNumeroPedido(),
                pedido.getDescripcion(),
                pedido.getCliente().getCasillero(),
                pedido.getSucursalOrigen() != null ? pedido.getSucursalOrigen().getNombre() : "N/A",
                pedido.getSucursalDestino() != null ? pedido.getSucursalDestino().getNombre() : "N/A",
                pedido.getTrackingExterno() != null ?
                        "<p><strong>Tracking externo:</strong> " + pedido.getTrackingExterno() + "</p>" : "",
                pedido.getCliente().getCasillero()
        );

        return base(contenido);
    }

    // ─── Template: paquete disponible para retiro ─────────────────────────────
    public String templateDisponible(Pedido pedido) {
        String contenido = """
            <h2>¡Tu paquete está listo para retiro! 🎁</h2>
            <p>Hola <strong>%s</strong>, ¡buenas noticias! Tu paquete ya está disponible:</p>

            <div class="info-box">
              <p><strong>Número de pedido:</strong> %s</p>
              <p><strong>Descripción:</strong> %s</p>
              <p><strong>Retira en:</strong> %s</p>
            </div>

            <p>📍 <strong>¿Qué necesitas para retirar?</strong></p>
            <ul>
              <li>Tu cédula o documento de identidad</li>
              <li>El número de pedido: <strong>%s</strong></li>
            </ul>

            <p style="color: #e53935; font-weight: bold;">
              ⚠️ Tienes 30 días para retirar tu paquete.
              Después de ese plazo se aplicarán cargos por almacenamiento.
            </p>

            <p style="text-align:center;">
              <a class="btn" href="#">Ver detalles del pedido</a>
            </p>
        """.formatted(
                pedido.getCliente().getNombres(),
                pedido.getNumeroPedido(),
                pedido.getDescripcion(),
                pedido.getSucursalDestino() != null ? pedido.getSucursalDestino().getNombre() +
                        " - " + pedido.getSucursalDestino().getCiudad() : "N/A",
                pedido.getNumeroPedido()
        );

        return base(contenido);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String obtenerIcono(EstadoPedido estado) {
        return switch (estado) {
            case REGISTRADO             -> "📋";
            case RECIBIDO_EN_SEDE       -> "📦";
            case EN_CONSOLIDACION       -> "📦";
            case EN_TRANSITO            -> "✈️";
            case EN_ADUANA              -> "🔍";
            case RETENIDO_ADUANA        -> "⚠️";
            case LIBERADO_ADUANA        -> "✅";
            case RECIBIDO_EN_MATRIZ     -> "🏢";
            case EN_DISTRIBUCION        -> "🚚";
            case DISPONIBLE_EN_SUCURSAL -> "🎁";
            case ENTREGADO              -> "✅";
            case DEVUELTO               -> "↩️";
            case EXTRAVIADO             -> "❌";
        };
    }

    private String obtenerDescripcion(EstadoPedido estado) {
        return switch (estado) {
            case RECIBIDO_EN_SEDE       -> "Tu paquete llegó a nuestra sede y está siendo procesado.";
            case EN_TRANSITO            -> "Tu paquete está en camino hacia Ecuador. ¡Ya casi llega!";
            case EN_ADUANA              -> "Tu paquete está siendo revisado por aduana. Este proceso puede tomar 1-3 días hábiles.";
            case RETENIDO_ADUANA        -> "Tu paquete requiere documentación adicional en aduana. Nos pondremos en contacto contigo.";
            case LIBERADO_ADUANA        -> "Tu paquete fue liberado de aduana y continúa su camino.";
            case RECIBIDO_EN_MATRIZ     -> "Tu paquete llegó a nuestra sede en Quito y será enviado a tu sucursal pronto.";
            case EN_DISTRIBUCION        -> "Tu paquete está en camino a tu sucursal de retiro.";
            case DISPONIBLE_EN_SUCURSAL -> "¡Tu paquete ya está listo para que lo retires en tu sucursal!";
            case ENTREGADO              -> "¡Tu paquete fue entregado exitosamente! Gracias por confiar en Equalatam.";
            case DEVUELTO               -> "Tu paquete fue devuelto. Por favor contáctanos para más información.";
            default                     -> "El estado de tu paquete ha sido actualizado.";
        };
    }
}