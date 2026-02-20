package com.equalatam.equlatam_backv2.despachos.entity;


import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "despachos")
@Getter @Setter
@NoArgsConstructor
public class Despacho {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Número único de despacho ─────────────────────────────────────────────
    // Formato: DES-2026-00001
    @Column(unique = true, nullable = false)
    private String numeroDespacho;

    // ─── Ruta dinámica: cualquier sucursal → cualquier sucursal ──────────────
    @ManyToOne
    @JoinColumn(name = "sucursal_origen_id", nullable = false)
    private Sucursal sucursalOrigen;

    @ManyToOne
    @JoinColumn(name = "sucursal_destino_id", nullable = false)
    private Sucursal sucursalDestino;

    // ─── Estado ───────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDespacho estado = EstadoDespacho.ABIERTO;

    // ─── Información del transporte ───────────────────────────────────────────
    private String aerolinea;
    private String numeroVuelo;         // AA-1234
    private String guiaAerea;          // Número de AWB (Air Waybill)
    private String numeroContenedor;    // Para envíos marítimos
    private String tipoTransporte;      // AEREO, MARITIMO, TERRESTRE

    // ─── Fechas ───────────────────────────────────────────────────────────────
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaSalidaProgramada;    // Cuándo está programado salir
    private LocalDateTime fechaSalidaReal;          // Cuándo salió realmente
    private LocalDateTime fechaLlegadaProgramada;   // Cuándo está programado llegar
    private LocalDateTime fechaLlegadaReal;         // Cuándo llegó realmente

    // ─── Totales (calculados al cerrar) ──────────────────────────────────────
    private Integer totalPedidos = 0;
    private Double pesoTotal = 0.0;
    private Double valorTotalDeclarado = 0.0;

    // ─── Pedidos incluidos en este despacho ───────────────────────────────────
    @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DespachoDetalle> detalles = new ArrayList<>();

    // ─── Empleado que creó el despacho ────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "creado_por_id")
    private User creadoPor;

    // ─── Observaciones ────────────────────────────────────────────────────────
    private String observaciones;

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
