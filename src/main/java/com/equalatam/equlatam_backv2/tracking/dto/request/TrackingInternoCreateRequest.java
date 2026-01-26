package com.equalatam.equlatam_backv2.tracking.dto.request;

import com.equalatam.equlatam_backv2.guias.Enums;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TrackingInternoCreateRequest {
    private UUID guiaId;
    private Enums.EstadoGuia estado;
    private String motivo;
    private String observacion;
}