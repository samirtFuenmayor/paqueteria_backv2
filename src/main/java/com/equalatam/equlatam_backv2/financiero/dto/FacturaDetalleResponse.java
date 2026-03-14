package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class FacturaDetalleResponse {
    private UUID id;
    private String descripcion;
    private Double cantidad;
    private Double precioUnitario;
    private Double descuento;
    private Double subtotal;
    private Boolean gravaIva;
    private Integer orden;
}