package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class PagoRequest {
    private UUID facturaId;
    private Double monto;
    private String formaPago;
    private String referencia;
    private String banco;
    private String comprobanteUrl;
    private String fechaPago;
    private String observaciones;
}