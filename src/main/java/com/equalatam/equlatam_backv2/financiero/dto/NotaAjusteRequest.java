package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class NotaAjusteRequest {
    private UUID facturaOrigenId;
    private String tipo;
    private String motivo;
    private Double monto;
    private Boolean aplicaIva;
}