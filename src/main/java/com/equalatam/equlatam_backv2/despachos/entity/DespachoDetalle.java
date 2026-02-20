package com.equalatam.equlatam_backv2.despachos.entity;

import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "despacho_detalles")
@Getter @Setter
@NoArgsConstructor
public class DespachoDetalle {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "despacho_id", nullable = false)
    private Despacho despacho;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false, updatable = false)
    private LocalDateTime agregadoEn = LocalDateTime.now();

    private String observacion;
}