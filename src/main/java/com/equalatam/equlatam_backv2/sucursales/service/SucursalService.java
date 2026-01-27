package com.equalatam.equlatam_backv2.sucursales.service;


import com.equalatam.equlatam_backv2.sucursales.Enums;
import com.equalatam.equlatam_backv2.sucursales.dto.request.SucursalCreateRequest;
import com.equalatam.equlatam_backv2.sucursales.dto.request.SucursalEstadoRequest;
import com.equalatam.equlatam_backv2.sucursales.dto.request.SucursalUpdateRequest;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository repository;

    public Sucursal crear(SucursalCreateRequest request) {

        Sucursal s = new Sucursal();
        s.setNombre(request.nombre());
        s.setDireccion(request.direccion());
        s.setCiudad(request.ciudad());
        s.setProvincia(request.provincia());
        s.setTelefono(request.telefono());
        s.setEstado(Enums.EstadoSucursal.ACTIVA);
        s.setFechaCreacion(LocalDateTime.now());

        return repository.save(s);
    }
    public List<Sucursal> getAll() {
        return repository.findAll();
    }

    public Sucursal findById(UUID id) {
        return repository.findById(id).orElseThrow();
    }

    public Sucursal actualizar(UUID id, SucursalUpdateRequest request) {

        Sucursal s = obtenerPorId(id);
        s.setNombre(request.nombre());
        s.setDireccion(request.direccion());
        s.setCiudad(request.ciudad());
        s.setProvincia(request.provincia());
        s.setTelefono(request.telefono());
        s.setFechaActualizacion(LocalDateTime.now());

        return repository.save(s);
    }

    public Sucursal cambiarEstado(UUID id, SucursalEstadoRequest request) {

        Sucursal s = obtenerPorId(id);
        s.setEstado(request.estado());

        return repository.save(s);
    }

    public Sucursal obtenerPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
    }

    public List<Sucursal> listar() {
        return repository.findAll();
    }

    public List<Sucursal> porEstado(Enums.EstadoSucursal estado) {
        return repository.findByEstado(estado);
    }
}