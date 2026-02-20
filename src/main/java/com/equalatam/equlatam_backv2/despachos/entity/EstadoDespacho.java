package com.equalatam.equlatam_backv2.despachos.entity;

public enum EstadoDespacho {
    ABIERTO,        // Se están agregando pedidos
    CERRADO,        // No se pueden agregar más pedidos, listo para salir
    EN_TRANSITO,    // Ya salió de la sucursal origen
    RECIBIDO,       // Llegó a la sucursal destino
    PROCESADO,      // Todos los pedidos fueron distribuidos
    CANCELADO       // Cancelado
}
