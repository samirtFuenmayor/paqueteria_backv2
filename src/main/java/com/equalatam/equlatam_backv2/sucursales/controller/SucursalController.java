package com.equalatam.equlatam_backv2.sucursales.controller;


import com.equalatam.equlatam_backv2.sucursales.dto.request.SucursalRequest;
import com.equalatam.equlatam_backv2.sucursales.dto.response.SucursalResponse;
import com.equalatam.equlatam_backv2.sucursales.entity.TipoSucursal;
import com.equalatam.equlatam_backv2.sucursales.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    // ─── Crear sucursal ───────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<SucursalResponse> create(@Valid @RequestBody SucursalRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.create(req));
    }

    // ─── Listar todas las activas ─────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<SucursalResponse>> findAll() {
        return ResponseEntity.ok(sucursalService.findAllActivas());
    }

    // ─── Listar todas incluyendo inactivas (solo admin) ───────────────────────
    @GetMapping("/todas")
    public ResponseEntity<List<SucursalResponse>> findAllIncluyendoInactivas() {
        return ResponseEntity.ok(sucursalService.findAll());
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(sucursalService.findById(id));
    }

    // ─── Filtrar por tipo ─────────────────────────────────────────────────────
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<SucursalResponse>> findByTipo(@PathVariable TipoSucursal tipo) {
        return ResponseEntity.ok(sucursalService.findByTipo(tipo));
    }

    // ─── Solo internacionales (para crear pedidos) ────────────────────────────
    @GetMapping("/internacionales")
    public ResponseEntity<List<SucursalResponse>> findInternacionales() {
        return ResponseEntity.ok(sucursalService.findInternacionales());
    }

    // ─── Solo nacionales (para destino de pedidos) ────────────────────────────
    @GetMapping("/nacionales")
    public ResponseEntity<List<SucursalResponse>> findNacionales() {
        return ResponseEntity.ok(sucursalService.findNacionales());
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody SucursalRequest req) {
        return ResponseEntity.ok(sucursalService.update(id, req));
    }

    // ─── Desactivar (soft delete) ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        sucursalService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Reactivar ────────────────────────────────────────────────────────────
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<SucursalResponse> reactivar(@PathVariable UUID id) {
        return ResponseEntity.ok(sucursalService.reactivar(id));
    }
}