package com.equalatam.equlatam_backv2.financiero.entity;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoCotizacion;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cotizaciones_financiero")
@Getter @Setter
@NoArgsConstructor
public class Cotizacion {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Número único ─────────────────────────────────────────────────────────
    // Formato: COT-2026-00001
    @Column(unique = true, nullable = false)
    private String numeroCotizacion;

    // ─── Relaciones ───────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Puede existir cotización sin pedido (cotización libre / proforma)
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "tarifa_id")
    private Tarifa tarifa;

    // ─── Datos del paquete al momento de cotizar ──────────────────────────────
    private Double pesoReal;            // libras
    private Double largo;               // cm
    private Double ancho;               // cm
    private Double alto;                // cm
    private Double valorDeclarado;      // USD

    // ─── Cálculos ─────────────────────────────────────────────────────────────
    private Double pesoVolumetrico;     // calculado
    private Double pesoFacturable;      // mayor entre real y volumétrico

    // ─── Montos ───────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private Double subtotal = 0.0;

    @Column(nullable = false)
    private Double porcentajeIva = 15.0;

    @Column(nullable = false)
    private Double montoIva = 0.0;

    @Column(nullable = false)
    private Double total = 0.0;

    // ─── Detalle de cálculo (JSON o texto para auditoría) ─────────────────────
    @Column(columnDefinition = "TEXT")
    private String detalleCalculo;      // JSON con el desglose para mostrar al cliente

    // ─── Estado y validez ─────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

    @Column(nullable = false)
    private LocalDate validaHasta;      // Por defecto: creación + 7 días

    // ─── Observaciones ────────────────────────────────────────────────────────
    private String observaciones;

    // ─── Auditoría ────────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "creado_por_id")
    private User creadoPor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}