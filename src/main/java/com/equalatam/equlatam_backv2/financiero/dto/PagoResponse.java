package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class PagoResponse {
    private UUID id;
    private String numeroPago;
    private String facturaNumero;
    private String clienteNombre;
    private Double monto;
    private String formaPago;
    private String referencia;
    private String banco;
    private String comprobanteUrl;
    private String estado;
    private String fechaPago;
    private String fechaConfirmacion;
    private String registradoPor;
    private String confirmadoPor;
    private String observaciones;
}