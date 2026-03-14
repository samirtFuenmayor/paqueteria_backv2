package com.equalatam.equlatam_backv2.financiero.entity;

import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoFactura;
import com.equalatam.equlatam_backv2.financiero.enums.TipoNota;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notas_ajuste")
@Getter @Setter
@NoArgsConstructor
public class NotaAjuste {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Número único formato SRI ─────────────────────────────────────────────
    @Column(unique = true, nullable = false)
    private String numeroNota;          // NC-001-001-000000001 / ND-001-001-000000001

    private String establecimiento;
    private String puntoEmision;
    private Long   secuencial;

    // ─── Tipo ─────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNota tipo;              // CREDITO o DEBITO

    // ─── Factura de origen ────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "factura_origen_id", nullable = false)
    private Factura facturaOrigen;

    // ─── Montos ───────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private Double subtotal = 0.0;

    @Column(nullable = false)
    private Double iva = 0.0;

    @Column(nullable = false)
    private Double total = 0.0;

    // ─── Motivo (requerido por SRI) ───────────────────────────────────────────
    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    // ─── Estado ───────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado = EstadoFactura.EMITIDA;

    // ─── Fechas ───────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private LocalDate fechaEmision;

    // ─── Auditoría ────────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "emitido_por_id")
    private User emitidoPor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}