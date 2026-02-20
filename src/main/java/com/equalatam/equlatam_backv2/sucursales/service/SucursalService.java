package com.equalatam.equlatam_backv2.sucursales.service;

import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.sucursales.dto.request.SucursalRequest;
import com.equalatam.equlatam_backv2.sucursales.dto.response.SucursalResponse;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.entity.TipoSucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    // ─── Crear ────────────────────────────────────────────────────────────────
    @Transactional
    public SucursalResponse create(SucursalRequest req) {

        if (sucursalRepository.existsByCodigo(req.codigo())) {
            throw new IllegalArgumentException("Ya existe una sucursal con el código: " + req.codigo());
        }

        if (sucursalRepository.existsByPrefijoCasillero(req.prefijoCasillero())) {
            throw new IllegalArgumentException("Ya existe una sucursal con el prefijo: " + req.prefijoCasillero());
        }

        Sucursal s = new Sucursal();
        mapToEntity(req, s);
        return SucursalResponse.from(sucursalRepository.save(s));
    }

    // ─── Listar todas las activas ─────────────────────────────────────────────
    public List<SucursalResponse> findAllActivas() {
        return sucursalRepository.findByActivaTrue()
                .stream()
                .map(SucursalResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Listar todas (incluyendo inactivas) ──────────────────────────────────
    public List<SucursalResponse> findAll() {
        return sucursalRepository.findAll()
                .stream()
                .map(SucursalResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────
    public SucursalResponse findById(UUID id) {
        return SucursalResponse.from(getSucursalOrThrow(id));
    }

    // ─── Buscar por tipo ──────────────────────────────────────────────────────
    public List<SucursalResponse> findByTipo(TipoSucursal tipo) {
        return sucursalRepository.findByTipo(tipo)
                .stream()
                .map(SucursalResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Buscar sucursales internacionales (para seleccionar al crear pedido) ─
    public List<SucursalResponse> findInternacionales() {
        return sucursalRepository.findByTipo(TipoSucursal.INTERNACIONAL)
                .stream()
                .map(SucursalResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Buscar sucursales nacionales (para destino final del pedido) ─────────
    public List<SucursalResponse> findNacionales() {
        return sucursalRepository.findByTipo(TipoSucursal.NACIONAL)
                .stream()
                .filter(s -> s.isActiva())
                .map(SucursalResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @Transactional
    public SucursalResponse update(UUID id, SucursalRequest req) {
        Sucursal s = getSucursalOrThrow(id);

        // Validar código único solo si cambió
        if (!s.getCodigo().equals(req.codigo()) && sucursalRepository.existsByCodigo(req.codigo())) {
            throw new IllegalArgumentException("Ya existe una sucursal con el código: " + req.codigo());
        }

        mapToEntity(req, s);
        return SucursalResponse.from(sucursalRepository.save(s));
    }

    // ─── Desactivar (soft delete) ─────────────────────────────────────────────
    @Transactional
    public void desactivar(UUID id) {
        Sucursal s = getSucursalOrThrow(id);
        s.setActiva(false);
        sucursalRepository.save(s);
    }

    // ─── Reactivar ────────────────────────────────────────────────────────────
    @Transactional
    public SucursalResponse reactivar(UUID id) {
        Sucursal s = getSucursalOrThrow(id);
        s.setActiva(true);
        return SucursalResponse.from(sucursalRepository.save(s));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private Sucursal getSucursalOrThrow(UUID id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
    }

    private void mapToEntity(SucursalRequest req, Sucursal s) {
        s.setNombre(req.nombre());
        s.setCodigo(req.codigo().toUpperCase());
        s.setTipo(req.tipo());
        s.setPais(req.pais());
        s.setCiudad(req.ciudad());
        s.setDireccion(req.direccion());
        s.setTelefono(req.telefono());
        s.setEmail(req.email());
        s.setResponsable(req.responsable());
        s.setPrefijoCasillero(req.prefijoCasillero().toUpperCase());
    }
}