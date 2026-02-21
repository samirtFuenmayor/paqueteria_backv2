package com.equalatam.equlatam_backv2.tarifas.service;


import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import com.equalatam.equlatam_backv2.tarifas.dto.request.TarifaRequest;
import com.equalatam.equlatam_backv2.tarifas.dto.response.TarifaResponse;
import com.equalatam.equlatam_backv2.tarifas.entity.Tarifa;
import com.equalatam.equlatam_backv2.tarifas.entity.TipoTarifa;
import com.equalatam.equlatam_backv2.tarifas.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional
    public TarifaResponse create(TarifaRequest req) {
        Tarifa t = new Tarifa();
        mapToEntity(req, t);
        return TarifaResponse.from(tarifaRepository.save(t));
    }

    @Transactional
    public TarifaResponse update(UUID id, TarifaRequest req) {
        Tarifa t = getOrThrow(id);
        mapToEntity(req, t);
        return TarifaResponse.from(tarifaRepository.save(t));
    }

    @Transactional
    public TarifaResponse toggleActiva(UUID id) {
        Tarifa t = getOrThrow(id);
        t.setActiva(!t.isActiva());
        return TarifaResponse.from(tarifaRepository.save(t));
    }

    public List<TarifaResponse> findAll() {
        return tarifaRepository.findAll().stream()
                .map(TarifaResponse::from).collect(Collectors.toList());
    }

    public List<TarifaResponse> findActivas() {
        return tarifaRepository.findByActivaTrue().stream()
                .map(TarifaResponse::from).collect(Collectors.toList());
    }

    public TarifaResponse findById(UUID id) {
        return TarifaResponse.from(getOrThrow(id));
    }

    public List<TarifaResponse> findByTipo(TipoTarifa tipo) {
        return tarifaRepository.findByTipoAndActivaTrue(tipo).stream()
                .map(TarifaResponse::from).collect(Collectors.toList());
    }

    public List<TarifaResponse> findBySucursal(UUID sucursalId) {
        return tarifaRepository.findActivasBySucursal(sucursalId).stream()
                .map(TarifaResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id) {
        Tarifa t = getOrThrow(id);
        t.setActiva(false);
        tarifaRepository.save(t);
    }

    private void mapToEntity(TarifaRequest req, Tarifa t) {
        t.setNombre(req.nombre());
        t.setDescripcion(req.descripcion());
        t.setTipo(req.tipo());
        t.setValorFijo(req.valorFijo());
        t.setValorPorcentaje(req.valorPorcentaje());
        t.setValorMinimo(req.valorMinimo());
        t.setValorMaximo(req.valorMaximo());
        t.setPesoMinimo(req.pesoMinimo());
        t.setPesoMaximo(req.pesoMaximo());
        t.setVigenciaDesde(req.vigenciaDesde());
        t.setVigenciaHasta(req.vigenciaHasta());

        if (req.sucursalOrigenId() != null) {
            sucursalRepository.findById(req.sucursalOrigenId())
                    .ifPresent(t::setSucursalOrigen);
        } else {
            t.setSucursalOrigen(null);
        }
    }

    private Tarifa getOrThrow(UUID id) {
        return tarifaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tarifa no encontrada: " + id));
    }
}
