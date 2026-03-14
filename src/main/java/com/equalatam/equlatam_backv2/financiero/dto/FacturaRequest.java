package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class FacturaRequest {
    private UUID clienteId;
    private UUID pedidoId;
    private UUID cotizacionId;
    private String establecimiento;
    private String puntoEmision;
    private String formaPago;
    private LocalDate fechaVencimiento;
    private String observaciones;
    private List<FacturaDetalleRequest> detalles;
}