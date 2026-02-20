package com.equalatam.equlatam_backv2.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter @Setter
@NoArgsConstructor
// En Permission.java - por si tiene referencia inversa
@JsonIgnoreProperties({"permissions"})
public class Permission {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;
}
