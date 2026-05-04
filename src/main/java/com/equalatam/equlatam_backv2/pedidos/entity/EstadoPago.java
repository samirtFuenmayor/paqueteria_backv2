package com.equalatam.equlatam_backv2.pedidos.entity;

public enum EstadoPago {
    PENDIENTE_COMPROBANTE,  // Pedido registrado, aún no sube comprobante
    COMPROBANTE_ENVIADO,    // Cliente/agente subió la imagen
    PAGO_VERIFICADO,        // Admin confirmó el pago
    PAGO_RECHAZADO          // Admin rechazó el comprobante
}