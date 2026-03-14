package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;

@Data
public class FacturaDetalleRequest {
    private String descripcion;
    private Double cantidad;
    private Double precioUnitario;
    private Double descuento;
    private Boolean gravaIva;
    private Integer orden;
}