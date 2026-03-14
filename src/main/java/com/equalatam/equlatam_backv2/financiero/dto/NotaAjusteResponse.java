package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class NotaAjusteResponse {
    private UUID id;
    private String numeroNota;
    private String tipo;
    private String facturaOrigenNumero;
    private String clienteNombre;
    private Double subtotal;
    private Double iva;
    private Double total;
    private String motivo;
    private String estado;
    private LocalDate fechaEmision;
}