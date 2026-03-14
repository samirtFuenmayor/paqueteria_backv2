package com.equalatam.equlatam_backv2.financiero.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "factura_detalles")
@Getter @Setter
@NoArgsConstructor
public class FacturaDetalle {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    // ─── Descripción del ítem ─────────────────────────────────────────────────
    @Column(nullable = false)
    private String descripcion;         // "Servicio de importación - 2.5 lbs"

    private String codigoProducto;      // Opcional: código interno del servicio

    // ─── Cantidades y precios ─────────────────────────────────────────────────
    @Column(nullable = false)
    private Double cantidad = 1.0;

    @Column(nullable = false)
    private Double precioUnitario;

    private Double descuento = 0.0;     // Descuento en USD (no porcentaje)

    // ─── IVA ──────────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean gravaIva = true;    // true = 15%, false = 0%

    // ─── Orden en la factura ──────────────────────────────────────────────────
    private Integer orden = 1;

    // ─── Constructor de conveniencia ──────────────────────────────────────────
    public FacturaDetalle(Factura factura, String descripcion,
                          Double cantidad, Double precioUnitario, boolean gravaIva) {
        this.factura        = factura;
        this.descripcion    = descripcion;
        this.cantidad       = cantidad;
        this.precioUnitario = precioUnitario;
        this.gravaIva       = gravaIva;
        this.descuento      = 0.0;
    }

    public Double getSubtotal() {
        double bruto = (precioUnitario != null ? precioUnitario : 0.0)
                * (cantidad       != null ? cantidad       : 1.0);
        double desc  = descuento       != null ? descuento      : 0.0;
        return Math.round((bruto - desc) * 100.0) / 100.0;
    }
}