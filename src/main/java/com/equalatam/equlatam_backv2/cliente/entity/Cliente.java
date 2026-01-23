package com.equalatam.equlatam_backv2.cliente.entity;

import com.equalatam.equlatam_backv2.cliente.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String identificacion; // cedula/ruc/pasaporte

    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;

    private String callePrincipal;
    private String calleSecundaria;

    private String nacionalidad;
    private String provincia;
    private String ciudad;

    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Enums.EstadoPersona estado = Enums.EstadoPersona.ACTIVO;

    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL)
    private List<Beneficiario> beneficiarios = new ArrayList<>();
}
