package com.equalatam.equlatam_backv2.pedidos.dto.request;


import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record PedidoRequest(

        @NotNull(message = "El tipo de pedido es obligatorio")
        TipoPedido tipo,

        @NotNull(message = "El cliente es obligatorio")
        UUID clienteId,

        // Tracking del proveedor externo (Amazon, FedEx, etc.)
        String trackingExterno,
        String proveedor,
        String urlTracking,

        @NotBlank(message = "La descripción del contenido es obligatoria")
        String descripcion,

        @Positive(message = "El peso debe ser mayor a 0")
        Double peso,

        Double largo,
        Double ancho,
        Double alto,

        @Positive(message = "El valor declarado debe ser mayor a 0")
        Double valorDeclarado,

        Integer cantidadItems,

        // Sede exterior donde llega (EEUU, Canadá)
        @NotNull(message = "La sucursal de origen es obligatoria")
        UUID sucursalOrigenId,

        // Sucursal en Ecuador donde el cliente retira
        @NotNull(message = "La sucursal de destino es obligatoria")
        UUID sucursalDestinoId,

        String observaciones,
        String notasInternas,
        String fotoUrl
) {}