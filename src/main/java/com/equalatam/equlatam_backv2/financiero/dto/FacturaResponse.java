package com.equalatam.equlatam_backv2.financiero.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class FacturaResponse {
    private UUID id;
    private String numeroFactura;
    private String tipoDocumento;
    private String clienteNombre;
    private String clienteIdentificacion;
    private String clienteEmail;
    private String clienteDireccion;
    private String pedidoNumero;
    private Double subtotal0;
    private Double subtotal15;
    private Double descuento;
    private Double iva;
    private Double total;
    private String formaPago;
    private String estado;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String observaciones;
    private String emisorRuc;
    private String emisorRazonSocial;
    private List<FacturaDetalleResponse> detalles;
    private String creadoEn;
}