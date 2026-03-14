package com.equalatam.equlatam_backv2.financiero.dto;

import com.equalatam.equlatam_backv2.financiero.enums.CategoriaPaquete;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TarifaRequest {
    private String nombre;
    private String descripcion;
    private CategoriaPaquete categoria;
    private TipoPedido tipoPedido;
    private Double precioBase;
    private Double precioPorLibra;
    private Double pesoMinimo;
    private Double precioPorCm3;
    private Double factorDivisorVolumetrico;
    private Double porcentajeSobreValorDeclarado;
    private Double pesoDesde;
    private Double pesoHasta;
    private Double porcentajeIva;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private Boolean activo;
}