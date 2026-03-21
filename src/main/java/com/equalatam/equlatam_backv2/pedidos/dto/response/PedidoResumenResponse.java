package com.equalatam.equlatam_backv2.pedidos.dto.response;

import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;

import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoResumenResponse(
        UUID id,
        String numeroPedido,
        TipoPedido tipo,
        String descripcion,
        EstadoPedido estadoLogistico,

        // Estado financiero
        String estadoFinanciero,   // SIN_COTIZAR | COTIZADO | PENDIENTE_PAGO | PAGADO | FACTURADO
        UUID cotizacionId,
        Double totalCotizado,
        UUID facturaId,
        String numeroFactura,
        Double totalFactura,

        LocalDateTime fechaRegistro
) {}