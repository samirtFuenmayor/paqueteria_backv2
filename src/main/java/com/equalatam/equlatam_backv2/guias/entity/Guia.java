package com.equalatam.equlatam_backv2.guias.entity;

import com.equalatam.equlatam_backv2.cliente.entity.Beneficiario;
import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.rutas.entity.Ruta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "guias")
@Getter
@Setter
@NoArgsConstructor
public class Guia {


    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String numeroGuia;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @ManyToOne
    private Beneficiario beneficiario;

    @ManyToOne
    private Ruta ruta;

    @Enumerated(EnumType.STRING)
    private Enums.EstadoGuia estado;

    private String descripcion;
    private Double peso;
    private Double valorDeclarado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
