package com.equalatam.equlatam_backv2.pedidos.dto.response;

import com.equalatam.equlatam_backv2.pedidos.entity.PedidoItem;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoProducto;
import java.util.UUID;

public record PedidoItemResponse(
        UUID id,
        TipoProducto tipoProducto,
        String descripcion,
        String trackingExterno,
        String proveedor,
        Double peso,
        Double valorDeclarado,
        Boolean llego,
        Boolean despachado,
        String observaciones
) {
    public static PedidoItemResponse from(PedidoItem i) {
        return new PedidoItemResponse(
                i.getId(),
                i.getTipoProducto(),
                i.getDescripcion(),
                i.getTrackingExterno(),
                i.getProveedor(),
                i.getPeso(),
                i.getValorDeclarado(),
                i.getLlego(),
                i.getDespachado(),
                i.getObservaciones()
        );
    }
}