package com.equalatam.equlatam_backv2.cliente.entity;

import com.equalatam.equlatam_backv2.entity.User;
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

    @Column(unique = true, nullable = false)
    private String numeroIdentificacion;   // username del cliente para login

    // ─── Datos personales ─────────────────────────────────────────────────────
    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;
    private LocalDate fechaNacimiento;

    // ─── Ubicación ────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private String pais;
    private String provincia;
    private String ciudad;
    private String direccion;

    // ─── Casillero ───────────────────────────────────────────────────────────
    @Column(unique = true)
    private String casillero;

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursalAsignada;

    // ─── Usuario de acceso (creado automáticamente al registrarse) ────────────
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // ─── Familia / titular ────────────────────────────────────────────────────
    // Si es null → es titular independiente
    // Si tiene valor → está "afiliado" a ese titular
    @ManyToOne
    @JoinColumn(name = "titular_id")
    private Cliente titular;

    // Relación con el titular: HIJO, CONYUGE, PADRE, MADRE, AMIGO, OTRO
    @Enumerated(EnumType.STRING)
    private Parentesco parentesco;

    // ─── Estado y auditoría ───────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    // Fecha en que el cliente completó su registro (puede ser igual a creadoEn)
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    private String observaciones;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}