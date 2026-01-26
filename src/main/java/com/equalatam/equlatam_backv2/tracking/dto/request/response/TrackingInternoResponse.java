package com.equalatam.equlatam_backv2.tracking.dto.request.response;

import com.equalatam.equlatam_backv2.guias.Enums;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor

public class TrackingInternoResponse {

    private UUID id;
    private UUID guiaId;
    private Enums.EstadoGuia estado;
    private String motivo;
    private String observacion;
    private LocalDateTime fechaRegistro;
}