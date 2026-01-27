package com.equalatam.equlatam_backv2.tracking.service;

import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import com.equalatam.equlatam_backv2.guias.repository.GuiaRepository;
import com.equalatam.equlatam_backv2.tracking.entity.TrackingInterno;
import com.equalatam.equlatam_backv2.tracking.repository.TrackingInternoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingAutomaticoService {

    private final TrackingInternoRepository repository;
    private final GuiaRepository guiaRepository;

    public void registrarEvento(
            Guia guia,
            Enums.EstadoGuia estado,
            String motivo,
            String observacion
    ) {

        TrackingInterno tracking = new TrackingInterno();
        tracking.setGuia(guia);
        tracking.setEstado(estado);
        tracking.setMotivo(motivo);
        tracking.setObservacion(observacion);

        repository.save(tracking);
    }
}
