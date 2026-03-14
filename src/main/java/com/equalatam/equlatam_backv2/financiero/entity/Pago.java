package com.equalatam.equlatam_backv2.financiero.entity;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoPago;
import com.equalatam.equlatam_backv2.financiero.enums.FormaPago;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
@Getter @Setter
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Número único ─────────────────────────────────────────────────────────
    @Column(unique = true, nullable = false)
    private String numeroPago;          // PAG-2026-00001

    // ─── Relaciones ───────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // ─── Monto ────────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private Double monto;

    // ─── Forma de pago ────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPago formaPago;

    // Referencia externa: número de transferencia, voucher tarjeta, depósito, etc.
    private String referencia;

    // Banco de origen (para transferencias y depósitos)
    private String banco;

    // URL de comprobante (foto de transferencia, voucher, etc.)
    private String comprobanteUrl;

    // ─── Estado ───────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    private String motivoRechazo;       // Si fue rechazado, el motivo

    // ─── Fechas ───────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private LocalDateTime fechaPago;    // Fecha en que se realizó el pago

    private LocalDateTime fechaConfirmacion; // Fecha en que el admin confirmó

    // ─── Auditoría ────────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private User registradoPor;

    @ManyToOne
    @JoinColumn(name = "confirmado_por_id")
    private User confirmadoPor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    private String observaciones;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}