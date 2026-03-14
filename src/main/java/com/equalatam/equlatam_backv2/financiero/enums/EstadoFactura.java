package com.equalatam.equlatam_backv2.financiero.enums;

public enum EstadoFactura {
    BORRADOR,   // En construcción, no emitida
    EMITIDA,    // Emitida, pendiente de pago
    PAGADA,     // Cobrada en su totalidad
    ANULADA,    // Anulada (requiere nota de crédito)
    VENCIDA     // Pasó fecha de vencimiento sin pago
}