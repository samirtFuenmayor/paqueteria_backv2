package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CalculoTarifaResponse {
    private UUID tarifaId;
    private String tarifaNombre;
    private Double pesoReal;
    private Double pesoVolumetrico;
    private Double pesoFacturable;
    private Double precioBase;
    private Double costoLibras;
    private Double costoVolumen;
    private Double costoSeguro;
    private Double subtotal;
    private Double iva;
    private Double total;
    private String desglose;
}