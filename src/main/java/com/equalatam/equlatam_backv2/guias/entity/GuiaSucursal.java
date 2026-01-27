package com.equalatam.equlatam_backv2.guias.entity;

import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "guia_sucursal")
@Getter
@Setter
@NoArgsConstructor
public class GuiaSucursal {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private Guia guia;

    @ManyToOne(optional = false)
    private Sucursal sucursal;

    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
}

