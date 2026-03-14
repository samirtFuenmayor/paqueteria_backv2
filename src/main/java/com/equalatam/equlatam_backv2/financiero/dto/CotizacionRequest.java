package com.equalatam.equlatam_backv2.financiero.dto;

import com.equalatam.equlatam_backv2.financiero.enums.CategoriaPaquete;
import lombok.Data;
import java.util.UUID;

@Data
public class CotizacionRequest {
    private UUID clienteId;
    private UUID pedidoId;
    private UUID tarifaId;
    private CategoriaPaquete categoria;
    private Double pesoReal;
    private Double largo;
    private Double ancho;
    private Double alto;
    private Double valorDeclarado;
    private String observaciones;
}