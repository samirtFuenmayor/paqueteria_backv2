package com.equalatam.equlatam_backv2.tracking.service;

import com.equalatam.equlatam_backv2.guias.entity.Guia;
import com.equalatam.equlatam_backv2.guias.repository.GuiaRepository;
import com.equalatam.equlatam_backv2.tracking.dto.request.TrackingInternoCreateRequest;
import com.equalatam.equlatam_backv2.tracking.dto.request.TrackingInternoUpdateRequest;
import com.equalatam.equlatam_backv2.tracking.dto.request.response.TrackingInternoResponse;
import com.equalatam.equlatam_backv2.tracking.entity.TrackingInterno;
import com.equalatam.equlatam_backv2.tracking.repository.TrackingInternoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingInternoService {

    private final TrackingInternoRepository repository;
    private final GuiaRepository guiaRepository;

    public TrackingInternoResponse crear(TrackingInternoCreateRequest request) {

        Guia guia = guiaRepository.findById(request.getGuiaId())
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));

        TrackingInterno tracking = new TrackingInterno();
        tracking.setGuia(guia);
        tracking.setEstado(request.getEstado());
        tracking.setMotivo(request.getMotivo());
        tracking.setObservacion(request.getObservacion());

        return map(repository.save(tracking));
    }

    public TrackingInternoResponse actualizar(UUID id,
                                              TrackingInternoUpdateRequest request) {

        TrackingInterno tracking = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tracking interno no encontrado"));

        tracking.setEstado(request.getEstado());
        tracking.setMotivo(request.getMotivo());
        tracking.setObservacion(request.getObservacion());

        return map(repository.save(tracking));
    }

    public List<TrackingInternoResponse> listarPorGuia(UUID guiaId) {
        return repository.findByGuia_Id(guiaId)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<TrackingInternoResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private TrackingInternoResponse map(TrackingInterno t) {
        TrackingInternoResponse r = new TrackingInternoResponse();
        r.setId(t.getId());
        r.setGuiaId(t.getGuia().getId());
        r.setEstado(t.getEstado());
        r.setMotivo(t.getMotivo());
        r.setObservacion(t.getObservacion());
        r.setFechaRegistro(t.getFechaRegistro());
        return r;
    }
}

