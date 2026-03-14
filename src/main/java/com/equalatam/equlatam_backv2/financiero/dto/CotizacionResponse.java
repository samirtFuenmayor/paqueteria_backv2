package com.equalatam.equlatam_backv2.financiero.dto;

import com.equalatam.equlatam_backv2.financiero.enums.CategoriaPaquete;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CotizacionResponse {
    private UUID id;
    private String numeroCotizacion;
    private String clienteNombre;
    private String clienteIdentificacion;
    private String pedidoNumero;
    private String tarifaNombre;
    private CategoriaPaquete categoria;
    private Double pesoReal;
    private Double pesoVolumetrico;
    private Double pesoFacturable;
    private Double subtotal;
    private Double porcentajeIva;
    private Double montoIva;
    private Double total;
    private String detalleCalculo;
    private String estado;
    private LocalDate validaHasta;
    private String observaciones;
    private String creadoEn;
}