package com.equalatam.equlatam_backv2.tarifas.entity;

import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tarifas")
@Getter @Setter
@NoArgsConstructor
public class Tarifa {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nombre;          // Ej: "Tarifa USA por libra"

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTarifa tipo;

    // Sucursal origen a la que aplica (null = aplica a todas)
    @ManyToOne
    @JoinColumn(name = "sucursal_origen_id")
    private Sucursal sucursalOrigen;

    // ─── Valores ──────────────────────────────────────────────────────────────
    private Double valorFijo;           // USD fijo (manejo, por pieza)
    private Double valorPorcentaje;     // % del valor declarado (seguro)
    private Double valorMinimo;         // Mínimo a cobrar aunque el cálculo sea menor
    private Double valorMaximo;         // Máximo a cobrar

    // ─── Rangos de peso ───────────────────────────────────────────────────────
    private Double pesoMinimo;          // Desde cuántas libras aplica
    private Double pesoMaximo;          // Hasta cuántas libras aplica

    // ─── Vigencia ─────────────────────────────────────────────────────────────
    private LocalDateTime vigenciaDesde;
    private LocalDateTime vigenciaHasta;

    @Column(nullable = false)
    private boolean activa = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
