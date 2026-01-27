package com.equalatam.equlatam_backv2.despacho.entity;

import com.equalatam.equlatam_backv2.despacho.EnumsDespacho;
import com.equalatam.equlatam_backv2.rutas.entity.Ruta;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "despachos")
@Getter
@Setter
@NoArgsConstructor
public class Despacho {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @Enumerated(EnumType.STRING)
    private EnumsDespacho.EstadoDespacho estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaCierre;
}