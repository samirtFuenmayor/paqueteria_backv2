package com.equalatam.equlatam_backv2.rutas.entity;

import com.equalatam.equlatam_backv2.rutas.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rutas")
@Getter
@Setter
@NoArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String origen;

    @Column(nullable = false)
    private String destino;

    @Enumerated(EnumType.STRING)
    private Enums.TipoRuta tipo;

    @Enumerated(EnumType.STRING)
    private Enums.EstadoRuta estado;

    private String descripcion;

    private LocalDateTime fechaCreacion;

}