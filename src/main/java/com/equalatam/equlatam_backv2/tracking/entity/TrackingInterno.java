package com.equalatam.equlatam_backv2.tracking.entity;

import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tracking_interno")
@Getter
@Setter
@NoArgsConstructor
public class TrackingInterno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "guia_id")
    private Guia guia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.EstadoGuia estado;

    @Column(nullable = false)
    private String motivo;

    private String observacion;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
