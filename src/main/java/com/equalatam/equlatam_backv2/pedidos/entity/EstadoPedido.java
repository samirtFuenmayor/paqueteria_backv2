package com.equalatam.equlatam_backv2.pedidos.entity;

public enum EstadoPedido {
    // ─── Estados en sede exterior ─────────────────────────────────────────────
    REGISTRADO,             // Cliente registró el pedido, aún no llega a la sede
    RECIBIDO_EN_SEDE,       // Llegó físicamente a la sede exterior (EEUU/Canadá)
    EN_CONSOLIDACION,       // Se está agrupando en un despacho

    // ─── Estados en tránsito ──────────────────────────────────────────────────
    EN_TRANSITO,            // Salió de la sede exterior hacia Ecuador
    EN_ADUANA,              // Retenido en aduana
    RETENIDO_ADUANA,        // Problema en aduana, requiere documentación
    LIBERADO_ADUANA,        // Liberado de aduana, continúa su camino

    // ─── Estados en Ecuador ───────────────────────────────────────────────────
    RECIBIDO_EN_MATRIZ,     // Llegó a sede Quito
    EN_DISTRIBUCION,        // En camino a sucursal destino
    DISPONIBLE_EN_SUCURSAL, // Listo para retiro en sucursal

    // ─── Estados finales ──────────────────────────────────────────────────────
    ENTREGADO,              // Cliente retiró el paquete
    DEVUELTO,               // Fue devuelto al remitente
    EXTRAVIADO              // Paquete perdido
}
