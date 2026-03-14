package com.equalatam.equlatam_backv2.financiero.controller;

import com.equalatam.equlatam_backv2.financiero.dto.TarifaRequest;
import com.equalatam.equlatam_backv2.financiero.entity.Tarifa;
import com.equalatam.equlatam_backv2.financiero.enums.CategoriaPaquete;
import com.equalatam.equlatam_backv2.financiero.service.TarifaService;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/financiero/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaService service;

    // GET /api/financiero/tarifas
    @GetMapping
    public ResponseEntity<List<Tarifa>> listarActivas() {
        return ResponseEntity.ok(service.listarActivas());
    }

    // GET /api/financiero/tarifas/todas
    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<Tarifa>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // GET /api/financiero/tarifas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // POST /api/financiero/tarifas
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tarifa> crear(@RequestBody TarifaRequest req) {
        return ResponseEntity.ok(service.crear(req));
    }

    // PUT /api/financiero/tarifas/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tarifa> actualizar(@PathVariable UUID id,
                                             @RequestBody TarifaRequest req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }

    // DELETE /api/financiero/tarifas/{id}  (soft delete — desactiva)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> desactivar(@PathVariable UUID id) {
        service.desactivar(id);
        return ResponseEntity.ok(Map.of("message", "Tarifa desactivada correctamente"));
    }

    // POST /api/financiero/tarifas/calcular  — preview sin crear cotización
    @PostMapping("/calcular")
    public ResponseEntity<?> calcular(@RequestBody CalculoRequest req) {
        Tarifa tarifa;
        if (req.tarifaId() != null) {
            tarifa = service.obtener(req.tarifaId());
        } else {
            tarifa = service.buscarTarifaAplicable(req.categoria(), req.tipoPedido(),
                            req.pesoReal() != null ? req.pesoReal() : 0.0)
                    .orElseThrow(() -> new RuntimeException(
                            "No existe tarifa para los parámetros indicados"));
        }

        var resultado = service.calcular(tarifa,
                req.pesoReal(), req.largo(), req.ancho(), req.alto(), req.valorDeclarado());

        return ResponseEntity.ok(Map.of(
                "tarifaId",       tarifa.getId(),
                "tarifaNombre",   tarifa.getNombre(),
                "pesoReal",       resultado.pesoReal(),
                "pesoVolumetrico",resultado.pesoVolumetrico(),
                "pesoFacturable", resultado.pesoFacturable(),
                "subtotal",       resultado.subtotal(),
                "iva",            resultado.iva(),
                "total",          resultado.total(),
                "desglose",       resultado.desglose()
        ));
    }

    record CalculoRequest(
            UUID tarifaId,
            CategoriaPaquete categoria,
            TipoPedido tipoPedido,
            Double pesoReal,
            Double largo,
            Double ancho,
            Double alto,
            Double valorDeclarado
    ) {}
}