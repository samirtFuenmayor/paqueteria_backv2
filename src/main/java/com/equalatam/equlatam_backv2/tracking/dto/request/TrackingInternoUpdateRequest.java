package com.equalatam.equlatam_backv2.tracking.dto.request;

import com.equalatam.equlatam_backv2.guias.Enums;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackingInternoUpdateRequest {
    private Enums.EstadoGuia estado;
    private String motivo;
    private String observacion;
}
