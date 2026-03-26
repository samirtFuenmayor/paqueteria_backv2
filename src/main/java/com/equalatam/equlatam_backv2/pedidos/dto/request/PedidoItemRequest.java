package com.equalatam.equlatam_backv2.pedidos.dto.request;

import com.equalatam.equlatam_backv2.pedidos.entity.TipoProducto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PedidoItemRequest(
        @NotNull(message = "El tipo de producto es obligatorio")
        TipoProducto tipoProducto,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        String trackingExterno,
        String proveedor,
        Double peso,
        Double valorDeclarado,
        String observaciones
) {}