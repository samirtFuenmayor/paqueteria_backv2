package com.equalatam.equlatam_backv2.guias.entity;

public enum EstadoGuia {
    GENERADA,       // Recién creada
    ASIGNADA,       // Asignada a un despacho
    EN_TRANSITO,    // Viajando con el despacho
    ENTREGADA,      // Paquete entregado
    ANULADA         // Guía cancelada
}
