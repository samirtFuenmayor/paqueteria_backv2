package com.equalatam.equlatam_backv2.tracking.entity;


import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tracking_eventos")
@Getter @Setter
@NoArgsConstructor
public class TrackingEvento {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Pedido al que pertenece este evento ──────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // ─── Estado en este momento ───────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    // ─── Descripción del evento ───────────────────────────────────────────────
    @Column(nullable = false)
    private String descripcion;

    // ─── Ubicación donde ocurrió el evento ────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    private String ubicacionDetalle;    // Ej: "Bodega 2, anaquel 5"

    // ─── Quién registró el evento ─────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private User registradoPor;

    // ─── Si el evento es visible para el cliente ──────────────────────────────
    @Column(nullable = false)
    private boolean visibleParaCliente = true;

    // ─── Nota interna (solo empleados) ────────────────────────────────────────
    private String notaInterna;

    // ─── Despacho relacionado ─────────────────────────────────────────────────
    private String numeroDespacho;      // Referencia al despacho si aplica

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEvento = LocalDateTime.now();
}