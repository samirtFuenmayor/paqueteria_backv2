package com.equalatam.equlatam_backv2.sucursales.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sucursales")
@Getter @Setter
@NoArgsConstructor
public class Sucursal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    // Código único de sucursal (ej: MIA-001, YYZ-001, UIO-MATRIZ)
    @Column(unique = true, nullable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSucursal tipo;

    // País donde opera la sucursal
    @Column(nullable = false)
    private String pais;

    // Ciudad
    @Column(nullable = false)
    private String ciudad;

    // Dirección física completa
    @Column(nullable = false)
    private String direccion;

    // Teléfono de contacto
    private String telefono;

    // Email de la sucursal
    private String email;

    // Responsable de la sucursal
    private String responsable;

    // Si la sucursal está activa o fue dada de baja
    @Column(nullable = false)
    private boolean activa = true;

    // Prefijo para generar casilleros de clientes (ej: "MIA", "YYZ", "UIO")
    @Column(unique = true, nullable = false)
    private String prefijoCasillero;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}