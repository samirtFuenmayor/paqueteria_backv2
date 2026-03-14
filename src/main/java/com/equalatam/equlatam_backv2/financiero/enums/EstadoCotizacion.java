// ─── EstadoCotizacion.java ───────────────────────────────────────────────────
package com.equalatam.equlatam_backv2.financiero.enums;

public enum EstadoCotizacion {
    PENDIENTE,   // Generada, esperando aprobación
    APROBADA,    // Cliente/admin aprobó
    FACTURADA,   // Ya se emitió factura sobre esta cotización
    VENCIDA,     // Pasó la fecha de validez sin aprobarse
    CANCELADA
}