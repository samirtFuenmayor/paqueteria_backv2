package com.equalatam.equlatam_backv2.sucursales.entity;

import com.equalatam.equlatam_backv2.sucursales.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sucursales")
@Getter
@Setter
@NoArgsConstructor
public class Sucursal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    private String ciudad;
    private String provincia;
    private String telefono;

    @Enumerated(EnumType.STRING)
    private Enums.EstadoSucursal estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}