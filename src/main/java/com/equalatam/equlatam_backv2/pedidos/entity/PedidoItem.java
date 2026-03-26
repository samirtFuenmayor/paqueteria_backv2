package com.equalatam.equlatam_backv2.pedidos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedido_items")
@Getter @Setter
@NoArgsConstructor
public class PedidoItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Tipo de producto
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProducto tipoProducto;

    // Descripción del producto
    @Column(nullable = false)
    private String descripcion;

    // Tracking individual (Amazon, FedEx, etc.)
    private String trackingExterno;
    private String proveedor;

    // Datos físicos
    private Double peso;
    private Double valorDeclarado;

    // Estado de llegada — el admin marca si llegó
    private Boolean llego = false;
    private Boolean despachado = false;

    private String observaciones;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();
}