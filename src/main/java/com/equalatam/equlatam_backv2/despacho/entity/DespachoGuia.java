package com.equalatam.equlatam_backv2.despacho.entity;

import com.equalatam.equlatam_backv2.guias.entity.Guia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "despacho_guias")
@Getter
@Setter
@NoArgsConstructor
public class DespachoGuia {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "despacho_id")
    private Despacho despacho;

    @ManyToOne
    @JoinColumn(name = "guia_id")
    private Guia guia;
}
