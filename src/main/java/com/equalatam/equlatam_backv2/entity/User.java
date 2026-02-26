package com.equalatam.equlatam_backv2.entity;

import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String correo;

    private String telefono;
    private String nacionalidad;
    private String provincia;
    private String ciudad;
    private String direccion;

    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String password;

    // ─── Si true → el usuario DEBE cambiar su contraseña al primer login ──────
    // true  = admin creó la cuenta con contraseña temporal
    // false = el cliente se registró solo (puso su propia contraseña)
    @Column(nullable = false)
    private boolean mustChangePassword = false;

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}