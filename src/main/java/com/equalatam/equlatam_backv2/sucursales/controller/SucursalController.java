package com.equalatam.equlatam_backv2.sucursales.controller;

import com.equalatam.equlatam_backv2.sucursales.Enums;
import com.equalatam.equlatam_backv2.sucursales.dto.request.SucursalCreateRequest;
import com.equalatam.equlatam_backv2.sucursales.dto.request.SucursalEstadoRequest;
import com.equalatam.equlatam_backv2.sucursales.dto.request.SucursalUpdateRequest;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService service;

    @PostMapping
    public Sucursal crear(@RequestBody SucursalCreateRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public Sucursal actualizar(
            @PathVariable UUID id,
            @RequestBody SucursalUpdateRequest request) {
        return service.actualizar(id, request);
    }

    @PutMapping("/{id}/estado")
    public Sucursal cambiarEstado(
            @PathVariable UUID id,
            @RequestBody SucursalEstadoRequest request) {
        return service.cambiarEstado(id, request);
    }

    @GetMapping
    public List<Sucursal> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Sucursal obtener(@PathVariable UUID id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/estado/{estado}")
    public List<Sucursal> porEstado(
            @PathVariable Enums.EstadoSucursal estado) {
        return service.porEstado(estado);
    }
}
