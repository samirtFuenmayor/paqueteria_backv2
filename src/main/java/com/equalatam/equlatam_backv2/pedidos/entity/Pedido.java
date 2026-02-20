package com.equalatam.equlatam_backv2.pedidos.entity;


import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter @Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Número de pedido único ───────────────────────────────────────────────
    // Formato: PED-2024-00001
    @Column(unique = true, nullable = false)
    private String numeroPedido;

    // ─── Tipo ─────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPedido tipo;

    // ─── Cliente propietario ──────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // ─── Tracking externo (Amazon, FedEx, UPS, etc.) ─────────────────────────
    private String trackingExterno;     // Número de tracking del proveedor
    private String proveedor;           // Amazon, eBay, FedEx, etc.
    private String urlTracking;         // Link al tracking del proveedor

    // ─── Descripción del contenido ────────────────────────────────────────────
    @Column(nullable = false)
    private String descripcion;

    private Double peso;                // En libras
    private Double largo;               // Dimensiones en cm
    private Double ancho;
    private Double alto;
    private Double valorDeclarado;      // En USD
    private Integer cantidadItems;

    // ─── Sucursales involucradas ──────────────────────────────────────────────
    // Donde llega el paquete primero (EEUU o Canadá)
    @ManyToOne
    @JoinColumn(name = "sucursal_origen_id")
    private Sucursal sucursalOrigen;

    // Sucursal en Ecuador donde el cliente retirará
    @ManyToOne
    @JoinColumn(name = "sucursal_destino_id")
    private Sucursal sucursalDestino;

    // ─── Estado actual ────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.REGISTRADO;

    // ─── Empleado que registró el pedido ─────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private User registradoPor;

    // ─── Fechas importantes ───────────────────────────────────────────────────
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private LocalDateTime fechaRecepcionSede;       // Cuando llegó a sede exterior
    private LocalDateTime fechaSalidaExterior;      // Cuando salió hacia Ecuador
    private LocalDateTime fechaLlegadaEcuador;      // Cuando llegó a Ecuador
    private LocalDateTime fechaDisponible;          // Cuando está listo para retiro
    private LocalDateTime fechaEntrega;             // Cuando fue entregado

    // ─── Observaciones y notas internas ──────────────────────────────────────
    private String observaciones;
    private String notasInternas;       // Solo visibles para empleados

    // ─── Fotos / evidencias ───────────────────────────────────────────────────
    private String fotoUrl;             // URL de foto del paquete al recibir

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}