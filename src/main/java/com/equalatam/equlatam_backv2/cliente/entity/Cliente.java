package com.equalatam.equlatam_backv2.cliente.entity;

import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter @Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Identificación ───────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIdentificacion tipoIdentificacion;

    // Cédula, RUC o Pasaporte — es el username del cliente
    @Column(unique = true, nullable = false)
    private String numeroIdentificacion;

    // ─── Datos personales ──────────────────────────────────────────────────────
    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;

    private LocalDate fechaNacimiento;

    // ─── Datos de ubicación ───────────────────────────────────────────────────
    @Column(nullable = false)
    private String pais;         // Ecuador, USA, Canada, etc.

    private String provincia;
    private String ciudad;
    private String direccion;

    // ─── Casillero asignado ───────────────────────────────────────────────────
    // Ejemplo: MIA-00123, YYZ-00456
    @Column(unique = true)
    private String casillero;

    // Sucursal exterior donde el cliente recibe sus paquetes
    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursalAsignada;

    // ─── Estado y auditoría ───────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    private String observaciones;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}