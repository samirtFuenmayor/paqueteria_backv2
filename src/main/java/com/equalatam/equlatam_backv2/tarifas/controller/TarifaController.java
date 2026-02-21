package com.equalatam.equlatam_backv2.tarifas.controller;


import com.equalatam.equlatam_backv2.tarifas.dto.request.TarifaRequest;
import com.equalatam.equlatam_backv2.tarifas.dto.response.TarifaResponse;
import com.equalatam.equlatam_backv2.tarifas.entity.TipoTarifa;
import com.equalatam.equlatam_backv2.tarifas.service.TarifaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaService tarifaService;

    @PostMapping
    public ResponseEntity<TarifaResponse> create(@Valid @RequestBody TarifaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifaService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<TarifaResponse>> findAll() {
        return ResponseEntity.ok(tarifaService.findAll());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<TarifaResponse>> findActivas() {
        return ResponseEntity.ok(tarifaService.findActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(tarifaService.findById(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TarifaResponse>> findByTipo(@PathVariable TipoTarifa tipo) {
        return ResponseEntity.ok(tarifaService.findByTipo(tipo));
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<TarifaResponse>> findBySucursal(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(tarifaService.findBySucursal(sucursalId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarifaResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody TarifaRequest req) {
        return ResponseEntity.ok(tarifaService.update(id, req));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TarifaResponse> toggleActiva(@PathVariable UUID id) {
        return ResponseEntity.ok(tarifaService.toggleActiva(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tarifaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}