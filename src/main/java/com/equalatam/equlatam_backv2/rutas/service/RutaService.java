package com.equalatam.equlatam_backv2.rutas.service;


import com.equalatam.equlatam_backv2.guias.entity.Guia;
import com.equalatam.equlatam_backv2.guias.repository.GuiaRepository;
import com.equalatam.equlatam_backv2.rutas.Enums;
import com.equalatam.equlatam_backv2.rutas.entity.Ruta;
import com.equalatam.equlatam_backv2.rutas.entity.dto.request.RutaCreateRequest;
import com.equalatam.equlatam_backv2.rutas.entity.dto.request.RutaEstadoRequest;
import com.equalatam.equlatam_backv2.rutas.entity.dto.request.RutaUpdateRequest;
import com.equalatam.equlatam_backv2.rutas.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RutaService {

    private final RutaRepository rutaRepository;
    private final GuiaRepository guiaRepository;

    public Ruta crear(RutaCreateRequest request) {
        Ruta ruta = new Ruta();
        ruta.setOrigen(request.origen());
        ruta.setDestino(request.destino());
        ruta.setTipo(request.tipo());
        ruta.setDescripcion(request.descripcion());
        ruta.setEstado(Enums.EstadoRuta.ACTIVA);
        ruta.setFechaCreacion(LocalDateTime.now());
        return rutaRepository.save(ruta);
    }

    public Ruta actualizar(UUID id, RutaUpdateRequest request) {
        Ruta ruta = obtenerPorId(id);
        ruta.setOrigen(request.origen());
        ruta.setDestino(request.destino());
        ruta.setTipo(request.tipo());
        ruta.setDescripcion(request.descripcion());
        return rutaRepository.save(ruta);
    }

    public Ruta cambiarEstado(UUID id, RutaEstadoRequest request) {
        Ruta ruta = obtenerPorId(id);
        ruta.setEstado(request.estado());
        return rutaRepository.save(ruta);
    }

    public Ruta asignarRutaAGuia(UUID guiaId, UUID rutaId) {
        Guia guia = guiaRepository.findById(guiaId)
                .orElseThrow(() -> new RuntimeException("Guía no existe"));

        Ruta ruta = obtenerPorId(rutaId);

        guia.setRuta(ruta);
        guiaRepository.save(guia);

        return ruta;
    }

    public Ruta obtenerPorId(UUID id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
    }

    public List<Ruta> listar() {
        return rutaRepository.findAll();
    }

    public List<Ruta> porTipo(Enums.TipoRuta tipo) {
        return rutaRepository.findByTipo(tipo);
    }

    public List<Ruta> porEstado(Enums.EstadoRuta estado) {
        return rutaRepository.findByEstado(estado);
    }
}