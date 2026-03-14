package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;

@Data
public class ResumenFinancieroResponse {
    private Double totalFacturadoMes;
    private Double totalCobradoMes;
    private Double totalPendienteCobro;
    private Long   facturasPendientes;
    private Long   facturasVencidas;
    private Long   pagosEnEspera;
    private Double ivaGeneradoMes;
}