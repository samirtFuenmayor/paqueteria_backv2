package com.equalatam.equlatam_backv2.cliente.entity;

import com.equalatam.equlatam_backv2.cliente.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "beneficiarios")
@Getter
@Setter
@NoArgsConstructor
public class Beneficiario {

    @Id
    @GeneratedValue
    private UUID id;

    private String identificacion;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;

    @Enumerated(EnumType.STRING)
    private Enums.TipoBeneficiario tipo;

    @Enumerated(EnumType.STRING)
    private Enums.EstadoPersona estado = Enums.EstadoPersona.ACTIVO;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente titular;
}
